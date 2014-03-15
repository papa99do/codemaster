function MainCtrl($scope) {
    $scope.editor = ace.edit("editor");
    $scope.editor.setTheme("ace/theme/chrome");
    $scope.editor.getSession().setMode("ace/mode/scala");
}

function SaveSnippetCtrl($scope) {
    $scope.saveSnippet = function() {
        $scope.snippet.code = $scope.editor.getValue();
        $scope.request = angular.copy($scope.snippet);
    };

}