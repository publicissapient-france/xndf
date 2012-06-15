define([
    'jquery',
    'Underscore',
    'Backbone',
    'models/expenses',
    'views/expense-item',
    'text!../../tpl/home.html' ],
    function ($, _, Backbone, Expenses, ExpenseItemView, template) {
        var HomeView = Backbone.View.extend({
            events:{
                "click #get":"render"
            },

            render:function () {
                this.setElement(_.template(template, this.model));
                var table = this.$(".container tbody");
                var data = this.model;
                data.fetch({success:function () {
                    table.empty();
                    _.each(data.models, function (expense) {
                        table.append(new ExpenseItemView({model:expense}).render().el);
                    });
                }});
                $('#content').replaceWith(this.el);
            }
        });

        var collection = new Expenses();
        var homeView = new HomeView({model:collection});
        return homeView;
    });