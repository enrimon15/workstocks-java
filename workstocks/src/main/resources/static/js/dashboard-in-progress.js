window.addEventListener('load', function () {

    let $checkboxQualification  = document.getElementById('check-qualification');
    let $endQualification = document.getElementById('end-qualification');
    if ($checkboxQualification != null) {
        if ($checkboxQualification.checked === true) $endQualification.disabled = true;

        $checkboxQualification.addEventListener('change', function() {
            if (this.checked) {
                $endQualification.disabled = true;
            } else {
                $endQualification.disabled = false;
            }
        });
    }

    ///////////

    let $checkboxExperience  = document.getElementById('check-experience');
    let $endExperience = document.getElementById('end-experience');
    if ($checkboxExperience != null) {
        if ($checkboxExperience.checked === true) $endExperience.disabled = true;

        $checkboxExperience.addEventListener('change', function() {
            if (this.checked) {
                $endExperience.disabled = true;
            } else {
                $endExperience.disabled = false;
            }
        });
    }

    ///////////

    let $checkboxCertification  = document.getElementById('no-end-certification');
    let $endCertification = document.getElementById('end-certification');
    if ($checkboxCertification != null) {
        if ($checkboxCertification.checked === true) $endCertification.disabled = true;

        $checkboxCertification.addEventListener('change', function() {
            if (this.checked) {
                $endCertification.disabled = true;
            } else {
                $endCertification.disabled = false;
            }
        });
    }

});