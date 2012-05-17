var j;
define([
    'jQuery',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-detail.html',
    'text!../../tpl/expenseline-detail.html'
], function($, _, Backbone, expense, template, lineTemplate){
    var ExpenseDetailsView = Backbone.View.extend({
        render:function (eventName) {
            console.dir(this.model);
            this.setElement(_.template(template, this.model.asJson()));
            var model=this.model;
            var table = this.$(".container tbody");
            table.empty();
            _.each(model.get('lines'), function(line){
                line.valueDate=new Date(line.valueDate).toString("dd-MM-yyyy");
                var el = $(_.template(lineTemplate, line));
                el.find('select option[value='+line.expenseType+']').attr('selected',true)
                table.append(el);

            });
            $('#content').replaceWith(this.el);
        },

    });
    return ExpenseDetailsView;
});
