window.addEventListener('load', function () {
	
    // tooltip
    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    });

    // add qualification
    let $qualificationButt = document.getElementById('add-qualification-butt');
    $qualificationButt.addEventListener('click', function (e) {
        let $qualification = document.getElementById('add-qualification');
        $qualification.style.display = $qualification.style.display === 'none' ? 'flex' : 'none';
        let $qualificationIcon = document.getElementById('add-qualification-icon');
        if ($qualificationIcon.classList.contains('ti-close')) {
            $qualificationIcon.classList.remove('ti-close');
            $qualificationIcon.classList.add('ti-plus');
        } else {
            $qualificationIcon.classList.remove('ti-plus');
            $qualificationIcon.classList.add('ti-close');
        }
    });
    ////////////////

    // add experience
    let $experienceButt = document.getElementById('add-experience-butt');
    $experienceButt.addEventListener('click', function (e) {
        let $experience = document.getElementById('add-experience');
        $experience.style.display = $experience.style.display === 'none' ? 'flex' : 'none';
        let $experienceIcon = document.getElementById('add-experience-icon');
        if ($experienceIcon.classList.contains('ti-close')) {
            $experienceIcon.classList.remove('ti-close');
            $experienceIcon.classList.add('ti-plus');
        } else {
            $experienceIcon.classList.remove('ti-plus');
            $experienceIcon.classList.add('ti-close');
        }
    });
    ////////////////

    // add skill
    let $skillBut = document.getElementById('add-skill-butt');
    $skillBut.addEventListener('click', function (e) {
        let $skill = document.getElementById('add-skill');
        $skill.style.display = $skill.style.display === 'none' ? 'flex' : 'none';
        let $skillIcon = document.getElementById('add-skill-icon');
        if ($skillIcon.classList.contains('ti-close')) {
            $skillIcon.classList.remove('ti-close');
            $skillIcon.classList.add('ti-plus');
        } else {
            $skillIcon.classList.remove('ti-plus');
            $skillIcon.classList.add('ti-close');
        }
    });
    ////////////////

    // add certificate
    let $certificateButt = document.getElementById('add-certificate-butt');
    $certificateButt.addEventListener('click', function (e) {
        let $certificate = document.getElementById('add-certificate');
        $certificate.style.display = $certificate.style.display === 'none' ? 'flex' : 'none';
        let $certificateIcon = document.getElementById('add-certificate-icon');
        if ($certificateIcon.classList.contains('ti-close')) {
            $certificateIcon.classList.remove('ti-close');
            $certificateIcon.classList.add('ti-plus');
        } else {
            $certificateIcon.classList.remove('ti-plus');
            $certificateIcon.classList.add('ti-close');
        }
    });
    ////////////////
});