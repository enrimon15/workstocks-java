package it.workstocks.dto.mapper;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import it.workstocks.dto.AddressDto;
import it.workstocks.dto.blog.CommentDto;
import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.review.ReviewDto;
import it.workstocks.dto.user.UserDto;
import it.workstocks.dto.user.applicant.ApplicantDto;
import it.workstocks.dto.user.applicant.BasicApplicant;
import it.workstocks.dto.user.applicant.SimpleApplicanDto;
import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.SimpleCompanyDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.entity.Address;
import it.workstocks.entity.blog.Comment;
import it.workstocks.entity.blog.News;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.company.WorkingPlace;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.entity.review.Review;
import it.workstocks.entity.user.User;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.entity.user.applicant.curricula.Certification;
import it.workstocks.entity.user.applicant.curricula.Experience;
import it.workstocks.entity.user.applicant.curricula.Qualification;
import it.workstocks.entity.user.applicant.curricula.Skill;
import it.workstocks.utils.FileUtils;

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
	
	ApplicantDto toApplicantDto(Applicant applicant);
	
	@Mapping(target = "photo", source = "avatar")
	SimpleApplicanDto toSimpleDto(Applicant applicant);

	LinkedHashSet<SimpleApplicanDto> toDtoApplicantCollection(Set<Applicant> applicants);

	@Mapping(target = "skills", ignore = true)
	@Mapping(target = "certifications", ignore = true)
	@Mapping(target = "qualifications", ignore = true)
	@Mapping(target = "experiences", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateApplicant(BasicApplicant applicantDto, @MappingTarget Applicant applicant);
	
	@Mapping(target = "skills", ignore = true)
	@Mapping(target = "certifications", ignore = true)
	@Mapping(target = "qualifications", ignore = true)
	@Mapping(target = "experiences", ignore = true)
	@Mapping(target = "photo", source = "avatar")
	BasicApplicant toBasicApplicant(Applicant entity);

	/*
	 * WORKING PLACE
	 */

	LinkedHashSet<WorkingPlaceDto> toDtoWorkingPlaceCollection(Set<WorkingPlace> workingPlaces);

	/*
	 * ADDRESS
	 */

	Address toEntity(AddressDto addressDto);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateAddress(AddressDto appladdressDtoicantDto, @MappingTarget Address address);
	
	AddressDto toDto(Address entity);

	/*
	 * JOB OFFER
	 */

	@Mapping(target = "address", source = "workingPlace.address")
	@Mapping(target = "company.email", source = "company.companyOwner.email")
	@Mapping(target = "company.photo", source = "company.companyOwner.avatar")
	JobOfferDto toDto(JobOffer jobOffer);

	JobOffer toEntity(JobOfferDto jobOfferDto);
	
	@Mapping(target = "company", qualifiedByName = "companyForJobOffer")
	SimpleJobOfferDto toSimpleDto(JobOffer jobOffer);

	LinkedHashSet<SimpleJobOfferDto> toDtoJobOfferCollection(Set<JobOffer> jobOffers);
	
	@Named("companyForJobOffer")
	@Mapping(target = "companyForm", ignore = true)
	@Mapping(target = "employeesNumber", ignore = true)
	@Mapping(target = "website", ignore = true)
	@Mapping(target = "photo", source = "companyOwner.avatar")
	SimpleCompanyDto toDtoForJobOffer(Company company);

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

	@Mapping(target = "photo", source = "image")
	NewsDto toDto(News news);

	LinkedHashSet<NewsDto> toDtoNewsCollection(Set<News> newsList);

	/*
	 * COMMENT
	 */

	Comment toEntity(CommentDto commentDto);

	LinkedHashSet<CommentDto> toDto(Set<Comment> comments);

	/*
	 * COMPANY
	 */
	@Mapping(target = "photo", source = "companyOwner.avatar")
	@Mapping(target = "address", source = "mainWorkingPlace.address")
	SimpleCompanyDto toSimpleDto(Company company);
	
	LinkedHashSet<SimpleCompanyDto> toDtoSet(Set<Company> companySet);
	
	@Mapping(target = "jobOffers", ignore = true)
	@Mapping(target = "email", source = "companyOwner.email")
	@Mapping(target = "address", source = "mainWorkingPlace.address")
	@Mapping(target = "photo", source = "companyOwner.avatar")
	CompanyDto toDto(Company company);

	/*
	 * REVIEW
	 */
	Review toEntity(ReviewDto dto);

	ReviewDto toDto(Review entity);
	
	/*
	 * OPTIONAL INTEGRATION
	 */
	
	default <T> T unwrapOptional(Optional<T> optional) {
	    return optional.orElse(null);
	}
	
	default <T> Optional<T> wrapOptional(T object) {
		if (object == null) return null;
	    return Optional.of(object);
	}
	
	/*
	 * BYTE[] TO BASE64
	 */
	
	default String getBase64(byte[] file) {
		if (file == null || file.length <= 0) return null;
		return FileUtils.getBase64FromByteArray(file);
	}

}
