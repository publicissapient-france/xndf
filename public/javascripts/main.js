// Filename: main.js
// Require.js allows us to configure shortcut alias
// There usage will become more apparent futher along in the tutorial.
require.config({
    paths: {
        text : "libs/require.text",
        i18n : "libs/require.i18n",

        //jquery: 'http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery',
        jquery: 'libs/jquery',
        //jquery: 'libs/zepto', -- not working for now, for saveFile in expense-details
        hogan: 'http://twitter.github.com/hogan.js/builds/2.0.0/hogan-2.0.0.js',
        Underscore: 'libs/underscore/underscore-min',
        Backbone: 'libs/backbone/backbone',
        Moment: 'libs/moment.min',
        translations : "i18n"
    },

    shim: {
        'jquery':{
            exports:'$'
        },
        'Underscore':{
            deps: ['jquery'],
            exports:'_'
        },
        'Backbone': {
            //These script dependencies should be loaded before loading
            //backbone.js
            deps: ['Underscore', 'jquery'],
            //Once loaded, use the global 'Backbone' as the
            //module value.
            exports: 'Backbone'
        }
    }
});

require([
    // Some plugins have to be loaded in order due to there non AMD compliance
    // Because these scripts are not "modules" they do not pass any values to the definition function below
    'jquery',
    'translations',
    'app'
], function($,translations,App){
    // The "app" dependency is passed in as "App"
    // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
    App.initialize();
});

