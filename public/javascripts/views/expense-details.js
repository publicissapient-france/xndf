define([
    'zepto',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-detail.html'
], function ($, _, Backbone, Expense, template) {
    var ExpenseDetailsView = Backbone.View.extend({
        events:{
            "click #put":"saveExpense",
            "click #add":"addLine",
            "click #home":"close"
        },

        initialize:function () {
            this.slot=this.options.slot;
            this.model.on("reset", this.render, this);
            this.model.on("sync", this.render, this);
            this.model.on("change", this.render, this);
        },
        renderTemplate:function (json) {
            var $expenseElement = $(_.template(template, json));
            _.each(json.lines, function (line, index) {
                $expenseElement.find('[name="expenseType"]')[index].value=line.expenseType;
            });
            return $expenseElement;
        },

        render:function () {
            this.$el.html(this.renderTemplate(this.model.toJSON()));
            this.slot.html(this.el);
            return this;
        },

        saveExpense:function () {
            var elements = $('#content td#expense-line');
            var view = this;
            var lines = _.map(elements, function (template) {
                t = $(template);
                return {
                    "expense":(+t.find('[name="amount"]').val()),
                    "description":t.find('[name="description"]').val(),
                    "valueDate":t.find('[name="valueDate"]').val(),
                    "expenseType":t.find('[name="expenseType"]').val(),
                    "account":"Xebia"
                };
            });
            if (this.model.isNew()) {
                this.model.unset('id')
            }
            this.model.save({
                startDate:$('[name="startDate"]').val(),
                endDate:$('[name="endDate"]').val(),
                total:$('[name="total"]').val(),
                lines:lines
            });
            return false;
        },
        addLine:function () {
            var defaultExpense = new Expense();
            this.model.set({lines: this.model.get('lines').concat(defaultExpense.defaults.lines)}) ;
        },
        close:function () {
            $(this.el).unbind();
            $(this.el).empty();
        }

    });
    return ExpenseDetailsView;
});
