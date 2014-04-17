function MainCtrl($scope, $http, $timeout) {

    $scope.searchRequest = {};
    $scope.langMode = {};

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

    $scope.setLangMode = function (mode) {
        mode = mode || $scope.langMode.selected || 'text';
        $scope.editor.session.setMode('ace/mode/' + mode);
        $scope.langMode.selected = mode;

        if ($scope.snippetManager) {
            if ($scope.snippetManager.snippetMap[mode]) {
                console.log("Templates loaded for mode: ", mode);
                $scope.templates = $scope.snippetManager.snippetMap[mode];
            } else {
                populateSnippets(mode, 100);
            }
        }

    };

    function populateSnippets(mode, dueTime) {
        console.log("Retry loading templates for mode: ", mode, " in ", dueTime, " ms.");
        $timeout(function() {
            if ($scope.snippetManager.snippetMap[mode]) {
                console.log("Templates loaded for mode: ", mode);
                $scope.templates = $scope.snippetManager.snippetMap[mode];
            } else {
                populateSnippets(mode, dueTime * 2);
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

            //$scope.snippetManager.register([], 'java');

        });

        $scope.editor = editor;
    }

    function newAlert(type, message) {
        $("#alert-area").append($("<div class='alert alert-" + type + " fade in' data-alert><p> " + message + " </p></div>"));
        $(".alert").delay(2000).fadeOut("slow", function () { $(this).remove(); });
    }
}