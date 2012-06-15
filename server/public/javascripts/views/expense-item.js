define([
    'jquery',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-list-item.html'
], function($, _, Backbone, expense, template){
    var ExpenseItemView = Backbone.View.extend({
        render:function (eventName) {
            var el=$('.container tbody');


            el.append(_.template( template,this.model.asJson()));
            return this;
        }
    });
    return ExpenseItemView;
});
