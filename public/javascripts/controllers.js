function MainCtrl($scope, $http, $timeout, $filter) {

    $scope.searchRequest = {};
    $scope.langMode = {templateRegistry: {}};

    initEditor();

    $http.get('/langmodes').success(function(data) {
        $scope.langMode.all = data;
        $scope.setLangMode();
    });

    $scope.search = function(tag) {
        $scope.snippets = [];
        $scope.selectedSnippetId = null;

        if (tag) $scope.searchRequest.tag = tag;

        $http.get('/snippets/' + $scope.searchRequest.tag).success(function(data){
            if (data && data[0]) {
                $scope.snippets = data;
                $scope.select(data[0].id);
            } else {
                $scope.setLangMode();
                $scope.editor.setValue('');
            }
        });
    };

    $scope.select = function(snippetId) {
        $scope.selectedSnippetId = snippetId;
        $http.get('/snippet/' + snippetId).success(function(data){
            $scope.setLangMode($scope.selectedSnippet().langMode);
            $scope.editor.setValue(data, -1);
        });
    };

    $scope.saveSnippet = function() {
        var url = $scope.snippet.id ? '/snippet?id=' + $scope.snippet.id : '/snippet'
        $http.post(url, $scope.snippet).success(function(data) {
            $('#save-snippet-modal').modal('hide');
            newAlert('success', 'Snippet saved successfully!');
        }).error(function(data) {
            $('#save-snippet-modal').modal('hide');
            newAlert('danger', 'Some error occurred! Please try again later.');
        });
    };

    $scope.deleteSnippet = function(snippetId) {
        $http.delete('/snippet/' + snippetId).success(function(){
            $scope.search();
        });
    }

    $scope.newSnippet = function() {
        $scope.snippet = {code : $scope.editor.getValue()};
    };

    $scope.updateSnippet = function() {
        var selectedSnippet = $scope.selectedSnippet();
        if (selectedSnippet) {
            $scope.snippet = angular.copy(selectedSnippet);
            $scope.snippet.tags = $scope.snippet.tags.join(' ');
        } else {
            $scope.snippet = {};
        }
        $scope.snippet.code = $scope.editor.getValue();
    };

    $scope.selectedSnippet = function() {
        return _.find($scope.snippets, function(s){return s.id === $scope.selectedSnippetId;});
    };

    $scope.newTemplate = function() {
        $scope.template = {mode: $scope.langMode.selected};
    };

    $scope.updateTemplate = function(template) {
        $scope.template = angular.copy(template);
        $scope.template.mode = $scope.langMode.selected;
        $('#save-template-modal').modal();
    };

    $scope.deleteTemplate = function(template) {
        $http.delete('/template/' + template.id).success(function(){
            $scope.snippetManager.unregister(template, template.mode);
            checkUnmasked(template.mode);
        });
    };

    function checkUnmasked(mode) {
        _.each($scope.langMode.templateRegistry[mode].overriddenMap, function(template) {
            if (!$scope.snippetManager.snippetNameMap[mode][template.name]) {
                $scope.snippetManager.register(template, mode);
                delete $scope.langMode.templateRegistry[mode].overriddenMap[template.name];
            }
        });
    }

    $scope.saveTemplate = function() {
        var url = $scope.template.id ? '/template?id=' + $scope.template.id : '/template';
        var mode = $scope.template.mode;
        $scope.template.name = $scope.template.tabTrigger;
        $http.post(url, $scope.template).success(function(data) {
            loadCustomTemplates(mode);
            checkUnmasked(mode);
            $('#save-template-modal').modal('hide');
            newAlert('success', 'Template saved successfully!');
        }).error(function(data) {
            $('#save-template-modal').modal('hide');
            newAlert('danger', 'Some error occurred! Please try again later.');
        });
    };

    $scope.setLangMode = function (mode) {
        mode = mode || $scope.langMode.selected || 'text';
        $scope.editor.session.setMode('ace/mode/' + mode);
        $scope.langMode.selected = mode;

        $scope.templates = null;
        loadTemplates(mode, 100, 5);
    };

    function loadCustomTemplates(mode) {
        var templateRegistry = $scope.langMode.templateRegistry;
        var url = 'templates/' + mode;
        if (templateRegistry[mode]) {
            var lastLoadedOn = templateRegistry[mode].lastLoadedOn;
            url += '?lastLoadedOn=' + $filter('date')(lastLoadedOn, 'yyyyMMddHHmmss');
        }

        $http.get(url).success(function(data) {
            templateRegistry[mode] = templateRegistry[mode] || {};
            templateRegistry[mode].lastLoadedOn = new Date();
            templateRegistry[mode].overriddenMap = templateRegistry[mode].overriddenMap || {};

            if (data && data.length > 0) {
                console.log('templates loaded: ' + data.length);

                _.each(data, function(template) {
                    if ($scope.snippetManager.snippetNameMap[mode][template.name]) {
                        templateRegistry[mode].overriddenMap[template.name] = $scope.snippetManager.snippetNameMap[mode][template.name];
                    }
                });

                $scope.snippetManager.register(data, mode);
                if (!$scope.templates) {
                    $scope.templates = $scope.snippetManager.snippetMap[mode];
                }
            }
        });
    }

    function loadTemplates(mode, dueTime, retryTimes) {
        if (!$scope.snippetManager || mode === 'text') return;

        if ($scope.langMode.templateRegistry[mode]) {
            $scope.templates = $scope.snippetManager.snippetMap[mode];
            return;
        }

        console.log("Retry loading templates for mode: ", mode, " in ", dueTime, " ms.");
        $timeout(function() {
            if ($scope.snippetManager.snippetMap[mode]) {
                console.log("Templates loaded for mode: ", mode);
                $scope.templates = $scope.snippetManager.snippetMap[mode];
                loadCustomTemplates(mode);
            } else if (retryTimes > 0) {
                loadTemplates(mode, dueTime * 2, retryTimes - 1);
            } else {
                loadCustomTemplates(mode);
            }
        }, dueTime);
    }


    function initEditor() {
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/chrome");
        editor.session.setUseWrapMode(true);
        editor.session.setWrapLimitRange(null, null);
        editor.setBehavioursEnabled(true);//auto pairing of quotes & brackets
        editor.setShowPrintMargin(false);
        editor.session.setUseSoftTabs(true);//use soft tabs (likely the default)

        //Include auto complete- Only for Template Editor page
        ace.config.loadModule('ace/ext/language_tools', function () {
            editor.setOptions({
                enableBasicAutocompletion: true,
                enableSnippets: true
            });

            $scope.snippetManager = ace.require("ace/snippets").snippetManager;
        });

        $scope.editor = editor;
    }

    function newAlert(type, message) {
        $("#alert-area").append($("<div class='alert alert-" + type + " fade in' data-alert><p> " + message + " </p></div>"));
        $(".alert").delay(2000).fadeOut("slow", function () { $(this).remove(); });
    }
}