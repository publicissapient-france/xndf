define([
    'zepto',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-detail.html'
], function ($, _, Backbone, expense, template) {
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
        },
        renderTemplate:function (json) {
            var $expenseElement = $(_.template(template, json));
            _.each(json.lines, function (line, index) {
                $expenseElement.find('#expenseType')[index].value=line.expenseType;
            });
            return $expenseElement;
        },

        render:function () {
            var $element = this.renderTemplate(this.model.toJSON());
            this.setElement($element);
            this.slot.html(this.el);
            return this;
        },

        saveExpense:function () {
            var elements = $('#content td#expense-line');
            var view = this;
            var lines = _.map(elements, function (template) {
                t = $(template);
                return {
                    "expense":(+t.find('#amount').val()),
                    "description":t.find('#description').val(),
                    "valueDate":t.find('#valueDate').val(),
                    "expenseType":t.find('#expenseType').val(),
                    "account":"Xebia"
                };
            });
            if (this.model.isNew()) {
                this.model.unset('id')
            }
            this.model.save({
                startDate:$('#startDate').val(),
                endDate:$('#endDate').val(),
                total:$('#total').val(),
                lines:lines
            });
            return false;
        },
        addLine:function () {
            var $table = $("#content table");
            var $element = this.renderTemplate(this.model.defaults);
            $table.append($element.find('table tbody tr'));
        },
        close:function () {
            $(this.el).unbind();
            $(this.el).empty();
        }

    });
    return ExpenseDetailsView;
});
