// Filename: router.js
define([
    'Backbone'
], function(Backbone){

    var AppRouter = Backbone.Router.extend({
        routes: {
            // Define some URL routes
            "home": "home",
            "expense/new":"new_expense",
            "expense/:id":"edit_expense",
            // Default
            "":"home",
            "*other"    : "defaultAction"
        },

        home: function(){

            if(this.homeview==undefined){
                var self=this;
                require(['views/home'], function(homeview){
                    self.homeview=homeview;
                });
            }else{
                this.homeview.refresh();
            }
        },
        edit_expense: function(id){
            require([ 'models/expense', 'views/expense-details'],function(Expense, ExpenseDetailsView){
                var expense = new Expense({id:id});
                expense.fetch({success:function() {
                    new ExpenseDetailsView({model:expense}).render();
                }})
            });
        },
        new_expense: function(id){
            require([ 'models/expense', 'views/expense-details'],function(Expense, ExpenseDetailsView){
                new ExpenseDetailsView({model:new Expense()}).render();
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