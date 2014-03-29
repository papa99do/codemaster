function MainCtrl($scope, $http) {
    $scope.editor = ace.edit("editor");
    $scope.editor.setTheme("ace/theme/chrome");
    $scope.editor.getSession().setMode("ace/mode/java");

    $scope.snippets = [];

    $scope.searchRequest = {};
    $scope.view = {};

    $scope.searchSnippets = function() {
        $http.get('/snippets/' + $scope.searchRequest.tag).success(function(data){
            $scope.snippets = data;
        });
    };

    $scope.showCode = function(snippetId) {
        $http.get('/snippet/' + snippetId).success(function(data){
            $scope.editor.setValue(data);
        });
    };
}

function SaveSnippetCtrl($scope, $http) {
    $scope.snippet = {};
    $scope.saveSnippet = function() {
        $scope.snippet.code = $scope.editor.getValue();
        $scope.request = angular.copy($scope.snippet);

        $http.post('/snippet', $scope.request).success(function(data) {
            $('#saveSnippetModal').modal('hide');
            // TODO show flash message for successful request
        });
    };

}