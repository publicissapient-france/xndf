// Filename: models/project
define([
    'Underscore',
    'Backbone',
    'Moment'
], function (_, Backbone,moment) {
    var Expense = Backbone.Model.extend({
            urlRoot:"/expenses",
            defaults:{
                startDate:moment().date(1),
                endDate:moment().date(1).add('months',1).add('days',-1),
                total:0,
                id:null,
                lines:[{
                    expense:"0.0",
                    description:"",
                    valueDate: moment(),
                    expenseType:"",
                    id:null
                }]
            },
            from:function () {
                var date = moment(this.get('startDate'));
                return date.format(window.dateFormat);
            },
            to:function () {
                var date = moment(this.get('endDate'));
                return date.format(window.dateFormat);
            },
            asJson:function () {
                var json = {
                    from:this.from(),
                    to:this.to(),
                    id:this.get('id'),
                    total:this.get('total'),
                    lines: this.get('lines')
                }
                return json;
            }
        })
        ;
// You usually don't return a model instantiated
    return Expense;
})
;