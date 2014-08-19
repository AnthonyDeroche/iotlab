$(function() {
	var label = $("#label"), desc = $("#desc"), allFields = $([]).add(label)
			.add(desc), tips = $(".validateTips");

	function updateTips(t) {
		tips.text(t).addClass("ui-state-highlight");
		setTimeout(function() {
			tips.removeClass("ui-state-highlight", 1500);
		}, 500);
	}

	function checkLength(o, n, min, max) {
		if (o.val().length > max || o.val().length < min) {
			o.addClass("ui-state-error");
			updateTips("Length of " + n + " must be between " + min + " and "
					+ max + ".");
			return false;
		} else {
			return true;
		}
	}

	function checkRegexp(o, regexp, n) {
		if (!(regexp.test(o.val()))) {
			o.addClass("ui-state-error");
			updateTips(n);
			return false;
		} else {
			return true;
		}
	}

	$("#dialog-form").dialog(
			{
				autoOpen : false,
				height : 350,
				width : 350,
				modal : true,
				buttons : {
					"Create an measure" : function() {
						var bValid = true;
						allFields.removeClass("ui-state-error");

						bValid = bValid
								&& checkLength(label, "Name of experiment", 1,
										15);
						bValid = bValid
								&& checkLength(desc, "Description", 0, 1000);

						if (bValid) {
							var form = $('<form action="get" method="post">' +
									  '<input type="text" name="label" value="'+ label.val() +'" />' +
									  '<input type="text" name="desc" value="' + desc.val() + '" />' +
									  '</form>');
									$('body').append(form);
									$(form).submit();
							$(this).dialog("close");
						}
					},
					Cancel : function() {
						$(this).dialog("close");
					}
				},
				close : function() {
					allFields.val("").removeClass("ui-state-error");
				}
			});

	$("#create-user").click(function() {
		$("#dialog-form").dialog("open");
	});
});