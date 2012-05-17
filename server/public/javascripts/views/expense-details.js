define([
    'jQuery',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-detail.html',
    'text!../../tpl/expenseline-detail.html'
], function ($, _, Backbone, expense, template, lineTemplate) {
    var ExpenseDetailsView = Backbone.View.extend({
        self:this,
        initialize:function () {
            this.model.bind("reset", this.render, this);
        },
        events:{
            "click #put":"saveExpense",
            "click #add":"addLine"
        },

        render:function (eventName) {
            console.dir(this.model);
            this.setElement(_.template(template, this.model.asJson()));
            var model = this.model;
            var table = this.$(".container tbody");
            var lines = model.get('lines');
            if (lines.length != 0) {
                table.empty();
            }
            _.each(model.get('lines'), function (line) {
                line.valueDate = moment(line.valueDate).format(window.dateFormat);
                var el = $(_.template(lineTemplate, line));
                el.find('select option[value=' + line.expenseType + ']').attr('selected', true)
                table.append(el);

            });
            $('#content').replaceWith(this.el);
        },

        saveExpense:function () {
            var elements = $('#content td#expense-line');
            var view =this;
            var lines = _.map(elements, function (template) {
                t = $(template);
                return {
                    "expense":(+t.find('#amount').val()),
                    "description":t.find('#description').val(),
                    "valueDate":moment(t.find('#valueDate').val(), window.dateFormat).format(moment.defaultFormat),
                    "expenseType":t.find('#expenseType').val(),
                    "id":null,
                    "expenseReportId":null,
                    "account":"Xebia"
                };
            });
            this.model.set();
            if (this.model.isNew()) {
                this.model.unset('id')
            }
            this.model.save({
                startDate:moment($('#startDate').val(), window.dateFormat).format(),
                endDate:moment($('#endDate').val(), window.dateFormat).format(),
                total:$('#total').val(),
                lines:lines
            });
            return false;
        },
        addLine:function(){
             var table=$("#content table");
            _.each( this.model.defaults.lines, function (line) {
                console.log(line);
                line.valueDate = moment().format(window.dateFormat);
                var el = $(_.template(lineTemplate, line));
                el.find('select option[value=' + line.expenseType + ']').attr('selected', true)
                table.append(el);
            })
        },
        close:function () {
            $(this.el).unbind();
            $(this.el).empty();
        }

    });
    return ExpenseDetailsView;
});
