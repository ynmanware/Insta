//http://plnkr.co/edit/EKY98fBocrvKaJS1DcKM?p=preview

var app = angular.module('plunker', []);

app.controller('MainCtrl', function($scope) {
  $scope.name = 'World';
  $scope.numbers = printNumbers(100);
});

function printNumbers(n) {
  var numbers = [];
  var i = 1;
  var k = 1;
  while (i < n + 1) {
    k = i;
    if ((i % 3 === 0) && (i % 5 === 0)) {
      k = "Instaclustr";
    } else if (i % 3 === 0) {
      k = "Insta";
    } else if (i % 5 === 0) {
      k = "clustr";
    }
    console.log(k);
    numbers.push(k);
    i++;
  }

  return numbers;

}