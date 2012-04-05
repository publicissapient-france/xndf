var AppRouter = Backbone.Router.extend({
    views : App.views,
    models: App.models,
    routes:{
        "":"home",
        "users":"userlist",
        "users/:id":"userdetail"
    },

    initialize:function () {
    },

    home:function () {
        // Since the home view never changes, we instantiate it and render it only once
        var users = new App.models.UserCollection();
        if (!this.usersListView) {
            this.usersListView = new App.views.UserListView({model:users});
            this.usersListView.render();
        }
        $('#content').html(this.usersListView.el);
    },

    userlist:function () {
        var users = new this.models.UserCollection();
        if (!this.usersListView) {
            this.usersListView = new this.views.UserListView({model:users});
            this.usersListView.render();
        }
        $('#content').html(this.usersListView.el);
    },

    userdetail:function (id) {
        var user = new this.models.User({id:id});
        user.fetch({
            success:function (data) {
                // Note that we could also 'recycle' the same instance of EmployeeFullView
                // instead of creating new instances
                $('#content').html(new this.views.UserDetailView({model:data}).render().el);
            }
        });
    }

});

tpl.loadTemplates(['home','user-list', 'user-details', 'user-list-item'],
    function () {
        app = new AppRouter();
        Backbone.history.start();
    });