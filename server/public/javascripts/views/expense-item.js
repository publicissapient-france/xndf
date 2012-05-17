define([
    'jQuery',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-list-item.html'
], function($, _, Backbone, expense, template){
    var ExpenseItemView = Backbone.View.extend({
        render:function (eventName) {
            var el=$('.container tbody');

            var json = {

                from: this.model.get('startDate').split("T")[0],
                to:this.model.get('endDate').split("T")[0],
                total: this.model.get('total')}
            el.append(_.template( template,json));
            return this;
        }
    });
    return ExpenseItemView;
});
