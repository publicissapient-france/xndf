define([
    'jquery',
    'Underscore',
    'Backbone',
    'models/expenses',
    'text!../../tpl/home.html' ],
    function ($, _, Backbone, Expenses, template) {
        var HomeView = Backbone.View.extend({

            events:{
                "click #get":"refresh",
                "click tr": "navigate_row"

            },
            navigate_row: function (event){
                $(event.currentTarget).find('a')[0].click();
            },
            initialize: function(){
                this.bind();
            },

            render: function () {
                this.$el.html(_.template(template,{'models':this.model.toJSON()}));
                this.slot.html(this.$el);
                this.delegateEvents();
                return this;
            },

            refresh: function(){
                this.model.fetch();
            },

            bind: function(){
                this.model.on('reset',this.render,this);
                this.model.on('add', this.render,this);
                this.model.on('remove',this.render, this);
            }
        });

        return new HomeView({model:new Expenses()});
    });