// Filename: collections/projects
define([
    'Underscore',
    'Backbone',
    'models/expense'
], function(_, Backbone, Expense){
    var Expenses = Backbone.Collection.extend({
        model: Expense,
        url:"/expenses"
    });

    return Expenses;
});