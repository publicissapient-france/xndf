// Filename: main.js
window.dateFormat="DD-MM-YYYY";
// Require.js allows us to configure shortcut alias
// There usage will become more apparent futher along in the tutorial.
require.config({
    paths: {
        jQuery: 'libs/jquery/jquery.amd.plugin',
        Underscore: 'libs/underscore/underscore.amd.plugin',
        Backbone: 'libs/backbone/backbone.amd.plugin'
    }

});

require([

    // Load our app module and pass it to our definition function
    'app',

    // Some plugins have to be loaded in order due to there non AMD compliance
    // Because these scripts are not "modules" they do not pass any values to the definition function below
    'order!assets/javascripts/libs/moment.min.js',
    'order!//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js',
    'order!libs/underscore/underscore-min',
    'order!libs/backbone/backbone-min'
], function(App){
    moment.defaultFormat="YYYY-MM-DDTHH:mm:ssZZ"
    // The "app" dependency is passed in as "App"
    // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
    App.initialize();
});

