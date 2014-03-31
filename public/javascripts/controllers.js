function MainCtrl($scope, $http) {
    $scope.editor = ace.edit("editor");
    $scope.editor.setTheme("ace/theme/chrome");
    $scope.editor.getSession().setMode("ace/mode/java");

    $scope.searchRequest = {};
    $scope.view = {};

    $scope.search = function(tag) {
        $scope.snippets = [];
        $scope.selectedSnippetId = null;

        if (tag) $scope.searchRequest.tag = tag;
        $http.get('/snippets/' + $scope.searchRequest.tag).success(function(data){
            $scope.snippets = data;
        });
    };

    $scope.selected = function(snippetId) {
        $scope.selectedSnippetId = snippetId;
        $http.get('/snippet/' + snippetId).success(function(data){
            $scope.editor.setValue(data);
        });
    };

    $scope.saveSnippet = function() {
        $scope.snippet.code = $scope.editor.getValue();
        var url = $scope.snippet.id ? '/snippet?id=' + $scope.snippet.id : '/snippet'
        $http.post(url, $scope.snippet).success(function(data) {
            $('#saveSnippetModal').modal('hide');
            // TODO show flash message for successful request
        });
    };

    $scope.newSnippet = function() {
        $scope.snippet = {};
    };

    $scope.updateSnippet = function() {
        var selectedSnippet = _.find($scope.snippets, function(s){return s.id === $scope.selectedSnippetId;});
        if (selectedSnippet) {
            $scope.snippet = angular.copy(selectedSnippet);
            $scope.snippet.tags = $scope.snippet.tags.join(' ');
        } else {
            $scope.snippet = {};
        }
    };
}