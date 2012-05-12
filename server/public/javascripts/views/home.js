define([
    'jQuery',
    'Underscore',
    'Backbone',
    'models/usercollection',
    'views/userdetails',
    'text!../../tpl/home.html'
], function($, _, Backbone, userCollection,userItemView, homeTemplate){
    var HomeView = Backbone.View.extend({
        el: $('#content'),
        tagName:'ul',
        className:'nav nav-list',

        render: function(){
            // Using Underscore we can compile our template with data
            var el=$(this.el);
            console.log("test " + el);
            var data = new userCollection();
            data.fetch({success: function(){
                console.log("in callback");
                _.each(data.models, function (user) {
                    el.append(new userItemView({model:user}).render().el);
                });
            }});
            var compiledTemplate = _.template( homeTemplate, data );
            // Append our compiled template to this Views "el"
            el.append( compiledTemplate );
        }
    });
    var homeView =  new HomeView;
    return homeView;
});