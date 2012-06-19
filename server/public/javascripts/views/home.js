define([
    'jquery',
    'Underscore',
    'Backbone',
    'models/expenses',
    'views/expense-item',
    'text!../../tpl/home.html' ],
    function ($, _, Backbone, Expenses, ExpenseItemView, template) {
        var HomeView = Backbone.View.extend({
            el: '#content',

            events:{
                "click #get":"refresh"
            },

            initialize: function(){
                this.model.on('reset',this.render,this);
                this.model.on('add', this.render,this);
                this.model.on('remove',this.render, this);
                this.refresh();
            },

            render: function () {
                this.el = _.template(template,this.model);
                $('#content').html(this.el);
                return this;
            },

            refresh: function(){
                this.model.fetch();
            }
        });

        return new HomeView({model:new Expenses()});
    });