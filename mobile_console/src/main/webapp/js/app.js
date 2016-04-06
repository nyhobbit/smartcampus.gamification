'use strict';

/* App Module */
var cp = angular.module('cp', [                          
	'localization',
	'ngRoute',
	'ngSanitize',
	
	'cpServices',
	'cpControllers',
	'cpFilters',
	'cpDirectives',
	
	'ngCookies',
	'xeditable',
	'dialogs',
	'ui.bootstrap',
	'base64',
	'djds4rce.angular-socialshare'
]);

cp.config(['$routeProvider', '$locationProvider',
    function($routeProvider, $locationProvider) {
  	$routeProvider
  		.when('/', {
    		templateUrl: 'partials/profile.html',
    		controller: 'MainCtrl',
    		controllerAs: 'main'
    	})
    	.when('/profile/:id', {
    		templateUrl: 'partials/profile.html',
    		controller: 'MainCtrl',
    		controllerAs: 'main'
    	})
    	.when('/challeng/:id', {
    		templateUrl: 'partials/challeng.html',
    		controller: 'MainCtrl',
    		controllerAs: 'main'
    	})
    	.when('/classification/:id', {
    		templateUrl: 'partials/classification.html',
    		controller: 'MainCtrl',
    		controllerAs: 'main'
    	})
    	.when('/rules', {
    		templateUrl: 'partials/game_rules.html',
    		controller: 'MainCtrl',
    		controllerAs: 'main'
    	})
    	.when('/privacy', {
    		templateUrl: 'partials/privacy_data.html',
    		controller: 'MainCtrl',
    		controllerAs: 'main'
    	})
    	.when('/prizes', {
    		templateUrl: 'partials/game_prizes.html',
    		controller: 'MainCtrl',
    		controllerAs: 'main'
    	})
    	/*.when('/viewall/prizes', {
    		templateUrl: 'partials/game_prizes.html',
    		controller: 'MainCtrl',
    		controllerAs: 'main'
    	})*/
    	.otherwise({
    		redirectTo:'/'
    	});
  			
  	$locationProvider.html5Mode(true); //.hashPrefix('!')
}]);
cp.config(['$compileProvider',
    function( $compileProvider )
    {  
		$compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|data|file):/);
        // Angular before v1.2 uses $compileProvider.urlSanitizationWhitelist(...)
    }
]);
cp.run(function($FB){
	  $FB.init('1694843157451296');
});	  