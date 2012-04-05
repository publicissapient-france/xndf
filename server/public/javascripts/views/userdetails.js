App.views.UserListView = Backbone.View.extend({

    tagName:'ul',

    className:'nav nav-list',

    initialize:function () {
        this.template = _.template(tpl.get('user-list'));

    },

    render:function (eventName) {
        var el=$(this.el);
        el.empty();
        var collection = this.model;
        collection.fetch({success: function(){
            _.each(collection.models, function (user) {
                el.append(new App.views.UserListItemView({model:user}).render().el);
            });
        }});
        return this;
    }
});

App.views.UserListItemView = Backbone.View.extend({

    tagName: "li",

    className:"list-item",

    initialize:function () {
        this.template = _.template(tpl.get('user-list-item'));
    },

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }

});

App.views.UserDetailView = Backbone.View.extend({

    tagName:"div", // Not required since 'div' is the default if no el or tagName specified

    initialize:function () {
        this.template = _.template(tpl.get('employee-details'));
    },

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }

});