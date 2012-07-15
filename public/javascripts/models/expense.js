// Filename: models/project
define([
    'Underscore',
    'Backbone'
], function (_, Backbone) {
    var Expense = Backbone.Model.extend({
            urlRoot:"/expenses",
            defaults:{
                startDate:new Date(),
                endDate:new Date(),
                total:0,
                id:null,
                lines:[{
                    expense:"0.0",
                    description:"",
                    valueDate: new Date(),
                    expenseType:"",
                    id:null
                }]
            }
        })
        ;
// You usually don't return a model instantiated
    return Expense;
})
;