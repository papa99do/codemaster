function MainCtrl($scope) {
    $scope.editor = ace.edit("editor");
    $scope.editor.setTheme("ace/theme/chrome");
    $scope.editor.getSession().setMode("ace/mode/scala");
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