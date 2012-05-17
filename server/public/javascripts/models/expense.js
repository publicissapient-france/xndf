// Filename: models/project
define([
    'Underscore',
    'Backbone'
], function(_, Backbone){
    var Expense = Backbone.Model.extend({
        urlRoot:"/expense",
        from:function(){
            var date = new Date(this.get('startDate'));
            return date.toString("dd-MM-yyyy");
        },
        to:function(){
            var date = new Date(this.get('endDate'));
            return date.toString("dd-MM-yyyy");
        },
        asJson:function(){
            var json={
                from:this.from(),
                to:this.to(),
                id:this.get('id'),
                total: this.get('total')
            }
            return json;
        }
    });
    // You usually don't return a model instantiated
    return Expense;
});