//noinspection JSUnresolvedVariable
App.models.User = Backbone.Model.extend({
    urlRoot:"http://localhost:9000/user/"
});

App.models.UserCollection = Backbone.Collection.extend({
    model:App.models.User,
    url:"http://localhost:9000/users"
});