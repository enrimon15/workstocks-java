package it.workstocks.dto.mapper;

import java.util.LinkedHashSet;
import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import it.workstocks.dto.AddressDto;
import it.workstocks.dto.blog.CommentDto;
import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.job.JobApplicationDto;
import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.registration.Registration;
import it.workstocks.dto.review.ReviewDto;
import it.workstocks.dto.review.ReviewKeyDto;
import it.workstocks.dto.user.UserDto;
import it.workstocks.dto.user.applicant.ApplicantDto;
import it.workstocks.dto.user.applicant.SimpleApplicantDto;
import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.CompanyOwnerDto;
import it.workstocks.dto.user.company.SimpleCompanyOwnerDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.entity.Address;
import it.workstocks.entity.application.JobApplication;
import it.workstocks.entity.blog.Comment;
import it.workstocks.entity.blog.News;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.company.WorkingPlace;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.entity.review.Review;
import it.workstocks.entity.review.ReviewKey;
import it.workstocks.entity.user.User;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.entity.user.applicant.curricula.Certification;
import it.workstocks.entity.user.applicant.curricula.Experience;
import it.workstocks.entity.user.applicant.curricula.Qualification;
import it.workstocks.entity.user.applicant.curricula.Skill;
import it.workstocks.entity.user.companyowner.CompanyOwner;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {
	
	/*
	 * USER
	 */

	UserDto toDto(User user);	
	
	User toEntity(UserDto userDto);
	
	/*
	 * APPLICANT
	 */
	
	ApplicantDto toDto(Applicant applicant);
	
	SimpleApplicantDto toSimpleDto(Applicant applicant);
	
	LinkedHashSet<SimpleApplicantDto> toDtoSimpleApplicantCollection(Set<Applicant> applicants);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "avatar", ignore = true)
	@Mapping(target = "curricula", ignore = true)
	void updateApplicant(SimpleApplicantDto applicantDto, @MappingTarget Applicant applicant);

	/*
	 * COMPANY OWNER
	 */
	
	@Mapping(target = "company.companyOwner", ignore = true)
	CompanyOwnerDto toDto(CompanyOwner companyOwner);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "company.id", ignore = true)
	@Mapping(target = "company.createdAt", ignore = true)
	@Mapping(target = "company.updatedAt", ignore = true)
	@Mapping(target = "company.companyOwner", ignore = true)
	@Mapping(target = "avatar", ignore = true)
    void updateCompanyOwner(SimpleCompanyOwnerDto companyOwnerDto, @MappingTarget CompanyOwner companyOwner);
	
	@Mapping(target = "company.mainWorkingPlace", ignore = true)
	@Mapping(target = "company.companyOwner", ignore = true)
	SimpleCompanyOwnerDto toSimpleDto(CompanyOwner companyOwner);
	
	/*
	 * WORKING PLACE
	 */
	
	WorkingPlaceDto toDto(WorkingPlace workingPlace);
	
	WorkingPlace toEntity(WorkingPlaceDto workingPlaceDto);
	
	LinkedHashSet<WorkingPlaceDto> toDtoWorkingPlaceCollection(Set<WorkingPlace> workingPlaces);
	
	/*
	 * ADDRESS
	 */

	@Mapping(source = "address", target = "street")
	Address addressFromRegistration(Registration registration);
	
	Address toEntity(AddressDto addressDto);

	/*
	 * JOB OFFER
	 */
	
	@BeforeMapping
	default void setSkills(JobOffer jobOffer, @MappingTarget JobOfferDto jobOfferDto) {
		if (jobOffer.getSkills() != null && !jobOffer.getSkills().isEmpty()) {
			jobOfferDto.setSkillFromFE(new String[jobOffer.getSkills().size()]);
			int cont = 0;
			
			for (Skill skill : jobOffer.getSkills()) {
				jobOfferDto.getSkillFromFE()[cont] = skill.getName();
				cont++;
			}
		}
    }
	@Mapping(target = "company.companyOwner.company", ignore = true)
	JobOfferDto toDto(JobOffer jobOffer);
	
	JobOffer toEntity(JobOfferDto jobOfferDto);
	
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "skills", ignore = true)
    void updateJobOfferEntity(JobOfferDto jobOfferDto, @MappingTarget JobOffer jobOffer);

	LinkedHashSet<JobOfferDto> toDtoJobOfferCollection(Set<JobOffer> jobOffers);
	
	/*
	 * JOB APPLICATIONS
	 */
	
	LinkedHashSet<JobApplicationDto> toDtoJobApplicationCollection(Set<JobApplication> applications);
	
	/*
	 * SKILL
	 */
	
	SkillDto toDto(Skill skill);
	
	Skill toEntity(SkillDto skillDto);
	
	LinkedHashSet<SkillDto> toDtoSkillCollection(Set<Skill> skills);
	
	/*
	 * QUALIFICATION
	 */
	
	QualificationDto toDto(Qualification qualification);
	
	Qualification toEntity(QualificationDto qualificationDto);

	LinkedHashSet<QualificationDto> toDtoQualificationCollection(Set<Qualification> qualifications);
	
	/*
	 * EXPERIENCE
	 */
	
	ExperienceDto toDto(Experience experience);
	
	Experience toEntity(ExperienceDto experienceDto);
	
	LinkedHashSet<ExperienceDto> toDtoExperienceCollection(Set<Experience> experiences);
	
	/*
	 * CERTIFICATION
	 */

	CertificationDto toDto(Certification certification);
	
	Certification toEntity(CertificationDto certificationDto);
	
	LinkedHashSet<CertificationDto> toDtoCertificationCollection(Set<Certification> certifications);
	
	/*
	 * NEWS
	 */
	
	News toEntity(NewsDto newsDto);

	NewsDto toDto(News news);
	
	LinkedHashSet<NewsDto> toDtoNewsCollection(Set<News> newsList);
	
	@Mapping(target = "recentComments", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "likes", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNewsEntity(NewsDto newsDto, @MappingTarget News news);
	
	/*
	 * COMMENT
	 */

	Comment toEntity(CommentDto commentDto);
	
	LinkedHashSet<CommentDto> toDto(Set<Comment> comments);

	/*
	 * COMPANY
	 */
	LinkedHashSet<CompanyDto> toDtoSet(Set<Company> companySet);
	
	/*
	 * REVIEW
	 */
	Review toEntity(ReviewDto dto);
	
	ReviewDto toDto(Review entity);
	
	ReviewKey toEntity(ReviewKeyDto dto);
	
}
