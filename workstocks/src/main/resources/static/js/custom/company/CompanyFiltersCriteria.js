document.addEventListener("DOMContentLoaded", function () {

    function analyzeFilters() {
        var topFilterLocation = $('#topFilterLocation').val();
        var topFilterCompanyName = $('#topFilterCompanyName').val();

        var company = {};

        if (topFilterLocation) {
            company.address = topFilterLocation;
        }

        if(topFilterCompanyName) {
            company.name = topFilterCompanyName;
        }

        $('#checkBoxFoundationYear').find('input[type=checkbox]').each(function () {
            if ($(this).prop('checked')) {
                company.foundationYear = aggregateFoundationYear($(this).val());
            }
        });

        $('#checkBoxFilterEmployees').find('input[type=checkbox]').each(function () {
            if ($(this).prop('checked')) {
                company.employeesNumber = aggregateEmployeesNumber($(this).val());
            }
        });

        console.log("PARAMETRI QUERY: ",company);
        return company;
    }
    
    function aggregateFoundationYear(checkBoxOption) {
		console.log("CHECKBOX OPTION", checkBoxOption, typeof checkBoxOption);
		switch(checkBoxOption) {
			case '0':
				return [{"operator" : "LESS","value" : 1980}];
			case '1':
				console.log("SONO OPZIONE 1");
				return [{"operator" : "GREATER_EQUAL","value" : 1980},{"operator" : "LESS_EQUAL","value" : 1999}];
			case '2':
				return [{"operator" : "GREATER_EQUAL","value" : 2000},{"operator" : "LESS_EQUAL","value" : 2009}];
			case '3':
				return [{"operator" : "GREATER_EQUAL","value" : 2010},{"operator" : "LESS_EQUAL","value" : 2021}];
			case '4':
				return [{"operator" : "GREATER_EQUAL","value" : 2022}];
			default:
				return "ERROR";
		}
	}
	
	function aggregateEmployeesNumber(checkBoxOption) {
		console.log("CHECKBOX OPTION", checkBoxOption, typeof checkBoxOption);
		switch(checkBoxOption) {
			case '0':
				return [{"operator" : "LESS","value" : 100}];
			case '1':
				console.log("SONO OPZIONE 1");
				return [{"operator" : "GREATER_EQUAL","value" : 100},{"operator" : "LESS_EQUAL","value" : 499}];
			case '2':
				return [{"operator" : "GREATER_EQUAL","value" : 500},{"operator" : "LESS_EQUAL","value" : 999}];
			case '3':
				return [{"operator" : "GREATER_EQUAL","value" : 1000},{"operator" : "LESS_EQUAL","value" : 1999}];
			case '4':
				return [{"operator" : "GREATER_EQUAL","value" : 2000}];
			default:
				return "ERROR";
		}
	}

    function clearAllFilters() {
        console.log('cleared');
        $('#topFilterJobTitle').val("");

        $('#topFilterLocation').val("");

        $('#topFilterCompanyName').val("");


        $('#checkBoxFoundationYear').find('input[type=checkbox]').each(function () {
            console.log("trovati i checkbox");
            if ($(this).prop('checked')) {
               $(this).prop('checked', false);
            }
        });

        $('#checkBoxFilterEmployees').find('input[type=checkbox]').each(function () {
            console.log("trovati i checkbox");
            if ($(this).prop('checked')) {
                $(this).prop('checked', false);
            }
        });

        filtersByAjaxCall(1);
    }

    function filtersByAjaxCall(pageNumber) {
        let body = { "pageNumber" : pageNumber, "filters" : analyzeFilters()};
		
		console.log("BODY AJAX FILTER", body);		
        $(document).ready(function () {

            $.ajax({
				type: "POST",
				contentType: "application/json",
                url: "/public/companies",
                data: JSON.stringify(body),
                success: function (data) {
	
					$('#table_data').html(data.data);
					if (data.totalPages == 0) data.totalPages = 1;
                	paginazione.bootpag({total: data.totalPages,page: pageNumber, maxVisible: 10});
                	console.log("RISULTATI WINDOW:",window.totalResults);
                	$('#totalResultsInfo').html(window.totalResults+" "+window.totalResultI18nString);
	    			//objThis.html("Page " + num); // or some ajax content loading...
                }
            });

        });
    }

    $('input[type="checkbox"]').on('change', function () {
        $('input[name="' + this.name + '"]').not(this).prop('checked', false);
        filtersByAjaxCall(1);
    });
  
    let paginazione = $('#page-selection').bootpag({
		total: 5,
		page: 1,
	}).on("page", function(event, num){
		filtersByAjaxCall(num);
	    // ... after content load -> change total to 10
	    
	 
	});
	
	filtersByAjaxCall(1);

    $('#category').select2({
        placeholder:  window.skillsFilterTranslation,
        allowClear: true
    });

    $('#searchButton').on("click",function () {
        filtersByAjaxCall(1);
    });

    $('#resetFilterButton').on("click", function() {
        clearAllFilters();
    });

    $('.keyPress').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
            filtersByAjaxCall(1);
        }
    });

});
