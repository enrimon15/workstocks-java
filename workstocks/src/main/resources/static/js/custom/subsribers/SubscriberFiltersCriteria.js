document.addEventListener("DOMContentLoaded", function () {

    function analyzeFilters() {
        var topFilterNameSurname = $('#topFilterNameSurname').val();
        var topFilterLocation = $('#topFilterLocation').val();
        var topFilterJobTitle = $('#topFilterJobTitle').val();
        var topFilterSkill = $('#topFilterSkills').val();

        var applicant = {};

        if (topFilterNameSurname) {
            applicant.nameOrSurname = topFilterNameSurname;
        }

        if (topFilterLocation) {
            applicant.address = topFilterLocation;
        }

        if(topFilterJobTitle) {
            applicant.jobTitle = topFilterJobTitle;
        }

        if(topFilterSkill) {
            applicant.skill = topFilterSkill;
        }

  

        $('#checkBoxFilterSalary').find('input[type=checkbox]').each(function () {
            console.log("trovati i checkbox");
            if ($(this).prop('checked')) {
                applicant.salary = aggregateSalary($(this).val());
            }
        });

        console.log("PARAMETRI QUERY: ",applicant);
        return applicant;
    }

	function aggregateSalary(checkBoxOption) {
		switch (checkBoxOption) {
			case '0':
				return [{ "operator": "LESS", "value": 5 }];
			case '1':
				return [{ "operator": "LESS", "value" : 10 }];
			case '2':
				return [{ "operator": "LESS", "value" : 20 }];
			case '3':
				return [{ "operator": "LESS", "value" : 30 }];
			case '4':
				return [{"operator": "GREATER_EQUAL", "value" : 30}];
			default:
				return "ERROR";
		}
	}
	
    function clearAllFilters() {
        console.log('cleared');
        $('#topFilterNameSurname').val("");

        $('#topFilterLocation').val("");

        $('#topFilterJobTitle').val("");

        $('#topFilterSkills').val("");

        $('#checkBoxFilterSalary').find('input[type=checkbox]').each(function () {
            console.log("trovati i checkbox");
            if ($(this).prop('checked')) {
                $(this).prop('checked', false);
            }
        });

      
        filtersByAjaxCall(1);
    }

    function filtersByAjaxCall(pageNumber) {
		let body = { "pageNumber": pageNumber, "filters": analyzeFilters()};
		
		$(document).ready(function() {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				url: "/public/applicants",
				data: JSON.stringify(body),
				success: function(data) {

					$('#table_data').html(data.data);
					if (data.totalPages == 0) data.totalPages = 1;
					paginazione.bootpag({ total: data.totalPages, page: pageNumber, maxVisible: 10 });
					$('#totalResultsInfo').html(window.totalResults + " " + window.totalResultI18nString);
					//objThis.html("Page " + num); // or some ajax content loading...
				}
			});

		});
    }

    $('input[type="checkbox"]').on('change', function () {
        $('input[name="' + this.name + '"]').not(this).prop('checked', false);
        filtersByAjaxCall(1);
    });

    
	//prepara l'oggetto bootpag di paginazione'
	let paginazione = $('#page-selection').bootpag({
		total: 5,
		page: 1,
	}).on("page", function(event, num) {
		//azioni da eseguire all'evento di cambio pagina '
		filtersByAjaxCall(num);
		// ... after content load -> change total to 10


	});

	//scatena la prima fetch al caricamento della pagina
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
