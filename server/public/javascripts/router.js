// Filename: router.js
define([
    'Backbone',
    'models/expense',
    'views/home'
], function(Backbone, Expense, homeview){

    var AppRouter = Backbone.Router.extend({
        routes: {
            // Define some URL routes
            "home": "home",
            "expense/new":"new_expense",
            "expense/:id":"edit_expense",
            // Default
            "":"home",
            "*other"    : "home"
        },

        home: function(){
            homeview.refresh();
        },

        edit_expense: function(id){
            require(['views/expense-details'],function(ExpenseDetailsView){
                var expense = homeview.model.get(id);
                expense.fetch({success:function() {
                    new ExpenseDetailsView({model:expense}).render();
                }})
            });
        },

        new_expense: function(id){
            require(['views/expense-details'],function(ExpenseDetailsView){
                new ExpenseDetailsView({model:new Expense()}).render();
            });
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