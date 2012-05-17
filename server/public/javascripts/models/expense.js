// Filename: models/project
define([
    'Underscore',
    'Backbone'
], function(_, Backbone){
    var Expense = Backbone.Model.extend({
        url:"/expense"
    });
    // You usually don't return a model instantiated
    return Expense;
});