// Filename: router.js
define([
    'jQuery',
    'Underscore',
    'Backbone',
    'views/home'
], function($, _, Backbone, homeView){
    var AppRouter = Backbone.Router.extend({
        routes: {
            // Define some URL routes
            "":"home",
            // Default
            "home": "home"
        },

        home: function(){
            homeView.render();
        },


        defaultAction: function(actions){
            // We have no matching route, lets just log what the URL was
            console.log('No route:', actions);
        }
    });

    var initialize = function(){
        var app_router = new AppRouter;
        Backbone.history.start();
    };
    return {
        initialize: initialize
    };
});