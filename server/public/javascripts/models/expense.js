// Filename: models/project
define([
    'Underscore',
    'Backbone',
    'Moment'
], function (_, Backbone,moment) {
    var Expense = Backbone.Model.extend({
            urlRoot:"/expenses",
            defaults:{
                startDate:moment().date(1).format(window.dateFormat),
                endDate:moment().date(1).add('months',1).add('days',-1).format(window.dateFormat),
                total:0,
                id:null,
                lines:[{
                    expense:"0.0",
                    description:"",
                    valueDate: moment().format(window.dateFormat),
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