define([
    'jquery',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-detail.html'
], function ($, _, Backbone, Expense, template) {

    var flashError=function(model, response){
        $("#flash").html("<p>Error</p>").addClass("error").delay(2500).slideUp(250)
    }

    var flashSuccess=function(model, response){
        $("#flash").html("<p>Success</p>").addClass("success").delay(2500).slideUp(250)
    }

    var ExpenseDetailsView = Backbone.View.extend({
        events:{
            "click #put":"saveExpense",
            "click #send":"sendExpense",
            "click #home":"close",
            "click #add_file":"addFile",
            "change header":"change",
            "change footer":"change",
            "click tr[name='line']":"editLine",
            "click #save_line":"saveLine",
            "change #line_form" :"changeLine",
            "change #hidden-file": "saveFile"
        },
        addFile:function(){
            $('#hidden-file')[0].disabled=false;
            $('#hidden-file').click();
            $('#hidden-file')[0].disabled=true;
        },
        editLine:function(event){
           var target=event.currentTarget;
           var index=target.attributes['data-id'].nodeValue;
           this.model.currentLine=this.model.get('lines')[index];
           this.refreshView();
        },
        changeLine:function(event){
            var target = event.target;
            var path = target.name.split('.');
            path.shift();
            var value=target.value;
            if(target.type=="number"){
                value=(+target.value);
            }
            this.setValue(this.model.currentLine, path.shift(),path,value);
        },
        change:function(event){
            // Apply the change to the model
            var target = event.target;
            var path = target.name.split('.');

            var value=target.value;
            if(target.type=="number"){
                value=(+target.value);
            }
            this.setValue(this.model.attributes, path.shift(),path,value);
        },
        setValue:function(object,key,path,value){
            var nextKey=path.shift();
            if(nextKey){
                setValue(object[key],nextKey, path, value);
            } else {
                object[key]=value;
            }
        },
        initialize:function () {
            this.slot=this.options.slot;
            this.model.on("reset sync change", this.refreshView, this);
        },

        renderTemplate:function (json) {
            var $expenseElement = $(_.template(template, json));
            $expenseElement.find('[name*="line.expenseType"]')[0].value=this.model.currentLine.expenseType;
            return $expenseElement;
        },

        refreshView:function () {
            var json = this.model.toJSON();
            json.currentLine = this.model.currentLine;
            this.$el.html(this.renderTemplate(json));
            this.delegateEvents();
        },

        render:function () {
            this.refreshView();
            $(this.slot).html(this.el);
            return this;
        },

        sendExpense:function () {
            this.model.set({status:"Submitted()"});
            $("#flash").html("<p>Working...</p>").removeClass("success error").slideDown(250)
            this.model.save({},{
                error:flashError,
                success:flashSuccess
            });
            return false;
        },
        saveExpense:function () {
            this.model.set({status:"Draft()"});
            $("#flash").html("<p>Working...</p>").removeClass("success error").slideDown(250)
            this.model.save({},{
                error:flashError,
                success:flashSuccess
            });
            return false;
        },

        saveLine:function () {
            this.model.saveCurrentLine();
            this.refreshView();
        },

        close:function () {
            this.$el.unbind();
            this.$el.empty();
            this.model.unbind();
        },

        saveFile:function(){
            var self = this;
            var data = new FormData();
            var file = $("#hidden-file")[0].files[0];
            data.append('file', file);
            $.ajax({
                url: '/evidences',
                type: 'POST',
                data: data,
                processData: false,
                cache: false,
                contentType: false
            })
                .done(function (data) {
                    self.model.currentLine.evidences= _.union(self.model.currentLine.evidences,data.success.map(function(d){return d.oid;}));
                })
                .fail(function () {
                });
        }

    });
    return ExpenseDetailsView;
});
