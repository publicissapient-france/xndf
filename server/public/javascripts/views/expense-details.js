define([
    'jquery',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-detail.html'
], function ($, _, Backbone, expense, template) {
    var ExpenseDetailsView = Backbone.View.extend({
        self:this,
        initialize:function () {
            this.model.on("reset", this.render, this);
            this.model.on("sync", this.render, this);
        },
        events:{
            "click #put":"saveExpense",
            "click #add":"addLine"
        },

        renderTemplate:function (json) {
            var $element = $(_.template(template, json));
            _.each(json.lines, function (line, index) {
                var selectedOption = $element.find('#expenseType').eq(index).children('option[value=' + line.expenseType + ']');
                selectedOption.attr('selected', true);
            });
            return $element;
        },

        render:function () {
            console.log("render expense");
            var $element = this.renderTemplate(this.model.toJSON());
            this.setElement($element);
            $('#content').replaceWith($element);
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
                    "id":null,
                    "expenseReportId":null,
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
