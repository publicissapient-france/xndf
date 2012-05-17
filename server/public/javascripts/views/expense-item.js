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
                url: this.model.url(),
                id: this.model.id,
                from: this.model.from(),
                to:this.model.to(),
                total: this.model.get('total')}
            el.append(_.template( template,json));
            return this;
        }
    });
    return ExpenseItemView;
});
