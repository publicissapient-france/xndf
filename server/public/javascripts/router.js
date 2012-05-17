// Filename: router.js
define([
    'jQuery',
    'Underscore',
    'Backbone'
], function($, _, Backbone ){
    var AppRouter = Backbone.Router.extend({
        routes: {
            // Define some URL routes
            "home": "home",
            "expense/:id":"edit_expense",
            // Default
            "":"home",
            "*other"    : "defaultAction"
        },

        home: function(){
            require([ 'views/home' ],function(homeView){
                homeView.render();
            });
        },
        edit_expense: function(id){
            require([ 'models/expense', 'views/expense-details'],function(Expense, ExpenseDetailsView){
                var expense = new Expense({id:id});
                expense.fetch({success:function() {
                    new ExpenseDetailsView({model:expense}).render();
                }})
            });
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