// Filename: models/project
define([
    'Underscore',
    'Backbone'
], function (_, Backbone) {
    var serverDate=function(date){
        return date.toISOString().substr(0,10);
    } ;
    var monthStart = function(date){
        return new Date(date.getFullYear(), date.getMonth(),1,12,00,00);
    };
    var monthEnd = function(date){
        return new Date(new Date(date.getFullYear(), date.getMonth()+1,1,0,0,0)-1);
    };

    var Expense = Backbone.Model.extend({
        urlRoot:"/expenses",
        initialize:function(){
            if(this.isNew()){
                this.addLine();
            }
        },
        addLine : function(){
           this.set('lines', this.get('lines').concat(
                [_.clone({
                    expense: 0.0,
                    description: " ",
                    valueDate: serverDate(new Date()),
                    expenseType: "Lodging",
                    account: "xebia"
                })]
            ));
        },
        defaults:{
            startDate:serverDate(monthStart(new Date())),
            endDate:serverDate(monthEnd(new Date())),
            total:0.0,
            lines:[]
        }
    });
    return Expense;
})
;