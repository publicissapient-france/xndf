// Filename: main.js
// Require.js allows us to configure shortcut alias
// There usage will become more apparent futher along in the tutorial.
require.config({
    paths: {
        //jquery: 'http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery',
        //jquery: 'libs/jquery-1.7.2.min',
        zepto: 'libs/zepto.min',
        hogan: 'http://twitter.github.com/hogan.js/builds/2.0.0/hogan-2.0.0.js',
        Underscore: 'libs/underscore/underscore-min',
        Backbone: 'libs/backbone/backbone',
        Moment: 'libs/moment.min'
    },

    shim: {
        'zepto':{
            exports:'$'
        },
        'Underscore':{
            deps: ['zepto'],
            exports:'_'
        },
        'Backbone': {
            //These script dependencies should be loaded before loading
            //backbone.js
            deps: ['Underscore', 'zepto'],
            //Once loaded, use the global 'Backbone' as the
            //module value.
            exports: 'Backbone'

        }
    }
});

require([
    // Some plugins have to be loaded in order due to there non AMD compliance
    // Because these scripts are not "modules" they do not pass any values to the definition function below
    'zepto',
    'app'
], function($,App){
    // The "app" dependency is passed in as "App"
    // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
    App.initialize();
});

