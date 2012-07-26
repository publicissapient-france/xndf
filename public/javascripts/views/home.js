define([
    'zepto',
    'Underscore',
    'Backbone',
    'models/expenses',
    'text!../../tpl/home.html' ],
    function ($, _, Backbone, Expenses, template) {
        var HomeView = Backbone.View.extend({
            el: '#content',

            events:{
                "click #get":"refresh"
            },

            initialize: function(){
                this.bind();
                this.refresh();
            },

            render: function () {
                this.el = _.template(template,{'models':this.model.toJSON()});
                $('#content').html(this.el);
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