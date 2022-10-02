document.addEventListener("DOMContentLoaded", function() {

	function analyzeFilters() {
		var topFilterJobTitle = $('#topFilterJobTitle').val();
		var topFilterLocation = $('#topFilterLocation').val();
		var topFilterCompanyName = $('#topFilterCompanyName').val();

		var job = {};

		if (topFilterJobTitle) {
			job.jobTitle = topFilterJobTitle;
		}

		if (topFilterLocation) {
			job.address = topFilterLocation;
		}

		if (topFilterCompanyName) {
			job.companyName = topFilterCompanyName;
		}


		$('#checkBoxFilterExperience').find('input[type=checkbox]').each(function() {
			if ($(this).prop('checked')) {
				job.experience = aggregateExperience($(this).val());
			}
		});

		$('#checkBoxFilterSalary').find('input[type=checkbox]').each(function() {
			if ($(this).prop('checked')) {
				job.salary = aggregateSalary($(this).val());
			}
		});

		$('#checkBoxFilterSkill').find('input[type=checkbox]').each(function() {
			if ($(this).prop('checked')) {
				job.skill = $(this).val();
			}
		});

		$('#checkBoxFilterOfferType').find('input[type=checkbox]').each(function() {
			if ($(this).prop('checked')) {
				job.offerType = $(this).val().toUpperCase().replace('_','');
			}
		});
		checkBoxFilterOfferType

		return job;
	}

	function aggregateSalary(checkBoxOption) {
		switch (checkBoxOption) {
			case '0':
				return [{ "operator": "LESS", "value": 5 }];
			case '1':
				return [{ "operator": "GREATER_EQUAL", "value" : 5 }, { "operator": "LESS_EQUAL", "value": 9 }];
			case '2':
				return [{ "operator": "GREATER_EQUAL", "value": 10 }, { "operator": "LESS_EQUAL", "value": 19 }];
			case '3':
				return [{ "operator": "GREATER_EQUAL", "value": 20 }, { "operator": "LESS_EQUAL", "value": 49 }];
			case '4':
				return [{"operator": "GREATER_EQUAL", "value" : 50}];
			default:
				return "ERROR";
		}
	}

	function aggregateExperience(checkBoxOption) {
		switch (checkBoxOption) {
			case '0':
				return [{ "operator": "EQUAL", "value": 0 }];
			case '1':
				return [{ "operator": "EQUAL", "value": 1 }];
			case '2':
				return [{ "operator": "EQUAL", "value": 2 }];
			case '3':
				return [{ "operator": "EQUAL", "value": 3 }];
			case '4':
				return [{
					"operator": "GREATER_EQUAL", "value" : 4}];
			default:
				return "ERROR";
		}
	}

	function clearAllFilters() {
		$('#topFilterJobTitle').val("");

		$('#topFilterLocation').val("");

		$('#topFilterCompanyName').val("");


		$('#checkBoxFilterExperience').find('input[type=checkbox]').each(function() {
			if ($(this).prop('checked')) {
				$(this).prop('checked', false);
			}
		});

		$('#checkBoxFilterSalary').find('input[type=checkbox]').each(function() {
			if ($(this).prop('checked')) {
				$(this).prop('checked', false);
			}
		});

		$('#checkBoxFilterSkill').find('input[type=checkbox]').each(function() {
			if ($(this).prop('checked')) {
				$(this).prop('checked', false);
			}
		});

		$('#checkBoxFilterOfferType').find('input[type=checkbox]').each(function() {
			if ($(this).prop('checked')) {
				$(this).prop('checked', false);
			}
		});


		filtersByAjaxCall(1);
	}

	function filtersByAjaxCall(pageNumber) {
		let body = { "pageNumber": pageNumber, "filters": analyzeFilters() };
		console.log('PREPARAZIONE BODY AJAX: ', JSON.stringify(body));

		$(document).ready(function() {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				url: "/public/job-offers",
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

	$('input[type="checkbox"]').on('change', function() {
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

	//inizializzazione della select2
	$('#category').select2({
		placeholder: window.skillsFilterTranslation,
		allowClear: true
	});

	//recepisce la pressione del bottone di avvio ricerca
	$('#searchButton').on("click", function() {
		filtersByAjaxCall(1);
	});

	//recepisce e scatena la pulizia dei filtri
	$('#resetFilterButton').on("click", function() {
		clearAllFilters();
	});

	//recepisce la pressione del tasto invio
	$('.keyPress').keypress(function(event) {
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if (keycode == '13') {
			filtersByAjaxCall(1);
		}
	});

});
