package org.catools.athena.rest.tms.mapper;

import com.google.common.collect.Sets;
import org.catools.athena.core.model.MetadataDto;
import org.catools.athena.core.model.ProjectDto;
import org.catools.athena.core.model.UserDto;
import org.catools.athena.core.model.VersionDto;
import org.catools.athena.rest.AthenaBaseTest;
import org.catools.athena.rest.core.builder.CoreBuilder;
import org.catools.athena.rest.core.entity.Project;
import org.catools.athena.rest.core.entity.User;
import org.catools.athena.rest.core.entity.Version;
import org.catools.athena.rest.core.service.ProjectService;
import org.catools.athena.rest.core.service.UserService;
import org.catools.athena.rest.core.service.VersionService;
import org.catools.athena.rest.tms.builder.TmsBuilder;
import org.catools.athena.rest.tms.entity.*;
import org.catools.athena.rest.tms.service.*;
import org.catools.athena.tms.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

class TmsMapperTest extends AthenaBaseTest {

    private static Project PROJECT;
    private static User USER;
    private static Version VERSION;
    private final static List<Status> STATUSES = new ArrayList<>();
    private static Priority PRIORITY;
    private static ItemType TYPE;

    @Autowired
    TmsMapper tmsMapper;

    @Autowired
    UserService userService;

    @Autowired
    ProjectService projectService;

    @Autowired
    VersionService versionService;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemTypeService itemTypeService;

    @Autowired
    PriorityService priorityService;

    @Autowired
    StatusService statusService;

    @Autowired
    TestCycleService testCycleService;

    @BeforeAll
    public void beforeAll() {
        final UserDto userDto = CoreBuilder.buildUserDto();
        userDto.setId(userService.save(userDto).getId());
        USER = CoreBuilder.buildUser(userDto);

        final ProjectDto projectDto = CoreBuilder.buildProjectDto();
        projectDto.setId(projectService.save(projectDto).getId());
        PROJECT = CoreBuilder.buildProject(projectDto);

        final VersionDto versionDto = CoreBuilder.buildVersionDto(projectDto);
        versionDto.setId(versionService.save(versionDto).getId());
        VERSION = CoreBuilder.buildVersion(versionDto, PROJECT);

        List<StatusDto> statusDtos = TmsBuilder.buildStatusDto();
        for (StatusDto statusDto : statusDtos) {
            statusDto.setId(statusService.save(statusDto).getId());
            STATUSES.add(TmsBuilder.buildStatus(statusDto));
        }

        final PriorityDto priorityDto = TmsBuilder.buildPriorityDto();
        priorityDto.setId(priorityService.save(priorityDto).getId());
        PRIORITY = TmsBuilder.buildPriority(priorityDto);

        final ItemTypeDto itemTypeDto = TmsBuilder.buildItemTypeDto();
        itemTypeDto.setId(itemTypeService.save(itemTypeDto).getId());
        TYPE = TmsBuilder.buildItemType(itemTypeDto);
    }

    @Test
    void testItemDtoToItem() {
        final Item item = TmsBuilder.buildItem(PROJECT, PRIORITY, TYPE, STATUSES.get(0), USER, Sets.newHashSet(VERSION));
        final ItemDto itemDto = tmsMapper.itemToItemDto(item);
        verifyItemsMatch(itemDto, item);
    }

    @Test
    void testItemToItemDto() {
        final ItemDto itemDto = itemService.save(TmsBuilder.buildItemDto(TmsBuilder.buildItem(PROJECT, PRIORITY, TYPE, STATUSES.get(0), USER, Sets.newHashSet(VERSION))));
        final Item item = tmsMapper.itemDtoToItem(itemDto);
        verifyItemsMatch(itemDto, item);

    }

    @Test
    void testCycleDtoToTestCycle() {
        final TestCycleDto testCycleDto = TmsBuilder.buildTestCycleDto(TmsBuilder.buildTestCycle(VERSION));
        final TestCycle testCycle = tmsMapper.testCycleDtoToTestCycle(testCycleDto);

        verifyTestCycleMatches(testCycle, testCycleDto);
    }

    @Test
    void testCycleToTestCycle() {
        final TestCycle testCycle = TmsBuilder.buildTestCycle(VERSION);
        final TestCycleDto testCycleDto = tmsMapper.testCycleToTestCycleDto(testCycle);

        verifyTestCycleMatches(testCycle, testCycleDto);
    }

    @Test
    void testExecutionDtoToTestExecution() {
        final TestCycle testCycle = TmsBuilder.buildTestCycle(VERSION);
        final TestCycleDto testCycleDto = TmsBuilder.buildTestCycleDto(testCycle);
        testCycle.setId(testCycleService.save(testCycleDto).getId());

        final ItemDto itemDto = itemService.save(TmsBuilder.buildItemDto(TmsBuilder.buildItem(PROJECT, PRIORITY, TYPE, STATUSES.get(0), USER, Sets.newHashSet(VERSION))));
        final Item item = tmsMapper.itemDtoToItem(itemDto);

        final TestExecution testExecution = TmsBuilder.buildTestExecution(testCycle, item, STATUSES.get(0), USER);
        final TestExecutionDto testExecutionDto = tmsMapper.testExecutionToTestExecutionDto(testExecution);
        verifyTestExecutionMatch(testExecutionDto, testExecution);
    }

    @Test
    void testExecutionToTestExecutionDto() {
        final TestCycle testCycle = TmsBuilder.buildTestCycle(VERSION);
        final TestCycleDto testCycleDto = TmsBuilder.buildTestCycleDto(testCycle);
        testCycle.setId(testCycleService.save(testCycleDto).getId());

        final ItemDto itemDto = itemService.save(TmsBuilder.buildItemDto(TmsBuilder.buildItem(PROJECT, PRIORITY, TYPE, STATUSES.get(0), USER, Sets.newHashSet(VERSION))));
        final Item item = tmsMapper.itemDtoToItem(itemDto);

        final TestExecutionDto testExecutionDto = TmsBuilder.buildTestExecutionDto(TmsBuilder.buildTestExecution(testCycle, item, STATUSES.get(0), USER));
        final TestExecution testExecution = tmsMapper.testExecutionDtoToTestExecution(testExecutionDto);
        verifyTestExecutionMatch(testExecutionDto, testExecution);
    }

    @Test
    void statusTransitionDtoToStatusTransition() {
        final ItemDto itemDto = itemService.save(TmsBuilder.buildItemDto(TmsBuilder.buildItem(PROJECT, PRIORITY, TYPE, STATUSES.get(0), USER, Sets.newHashSet(VERSION))));
        final Item item = tmsMapper.itemDtoToItem(itemDto);

        final StatusTransition statusTransition = TmsBuilder.buildStatusTransition(STATUSES, item);
        final StatusTransitionDto statusTransitionDto = tmsMapper.statusTransitionToStatusTransitionDto(statusTransition);
        verifyStatusTransitionSetMatch(statusTransitionDto, statusTransition);
    }

    @Test
    void statusTransitionToStatusTransitionDto() {
        final Item item = TmsBuilder.buildItem(PROJECT, PRIORITY, TYPE, STATUSES.get(0), USER, Sets.newHashSet(VERSION));
        itemService.save(TmsBuilder.buildItemDto(item));

        final StatusTransition statusTransition = TmsBuilder.buildStatusTransition(STATUSES, item);
        final StatusTransitionDto statusTransitionDto = tmsMapper.statusTransitionToStatusTransitionDto(statusTransition);
        verifyStatusTransitionSetMatch(statusTransitionDto, statusTransition);
    }

    private void verifyTestExecutionMatch(TestExecutionDto t1, TestExecution t2) {
        assertThat(t1, notNullValue());
        assertThat(t2, notNullValue());
        assertThat(t2.getId(), equalTo(t1.getId()));
        assertThat(t2.getCreatedOn(), equalTo(t1.getCreatedOn()));
        assertThat(t2.getExecutedOn(), equalTo(t1.getExecutedOn()));
        assertThat(t2.getItem().getCode(), equalTo(t1.getItem()));
        assertThat(t2.getExecutor().getName(), equalTo(t1.getExecutor()));
        assertThat(t2.getStatus().getCode(), equalTo(t1.getStatus()));
        assertThat(t2.getCycle().getCode(), equalTo(t1.getCycle()));
    }

    private static void verifyTestCycleMatches(TestCycle t1, TestCycleDto t2) {
        assertThat(t1, notNullValue());
        assertThat(t2, notNullValue());
        assertThat(t2.getCode(), equalTo(t1.getCode()));
        assertThat(t2.getName(), equalTo(t1.getName()));
        assertThat(t2.getStartDate(), equalTo(t1.getStartDate()));
        assertThat(t2.getEndDate(), equalTo(t1.getEndDate()));
        assertThat(t2.getVersion(), equalTo(t1.getVersion().getCode()));
    }

    private static void verifyItemsMatch(ItemDto t1, Item t2) {
        assertThat(t1, notNullValue());
        assertThat(t2, notNullValue());

        assertThat(t1.getCode(), equalTo(t2.getCode()));
        assertThat(t1.getName(), equalTo(t2.getName()));
        assertThat(t1.getCreatedOn(), equalTo(t2.getCreatedOn()));
        assertThat(t1.getUpdatedOn(), equalTo(t2.getUpdatedOn()));

        assertThat(t1.getCreatedBy(), equalTo(t2.getCreatedBy().getName()));
        assertThat(t1.getUpdatedBy(), equalTo(t2.getUpdatedBy().getName()));

        assertThat(t1.getType(), equalTo(t2.getType().getCode()));
        assertThat(t1.getStatus(), equalTo(t2.getStatus().getCode()));
        assertThat(t1.getPriority(), equalTo(t2.getPriority().getCode()));
        assertThat(t1.getProject(), equalTo(t2.getProject().getCode()));

        Set<String> actualVersions = t1.getVersions();
        assertThat(actualVersions, notNullValue());
        assertThat(actualVersions.size(), equalTo(1));
        assertThat(actualVersions.stream().findFirst().orElse(null), equalTo(t2.getVersions().stream().findFirst().map(Version::getCode).orElse("")));

        verifyMetadataSetMatch(t1.getMetadata(), t2.getMetadata());
    }

    private static void verifyMetadataSetMatch(Set<MetadataDto> t1, Set<ItemMetadata> t2) {
        assertThat(t1, notNullValue());
        assertThat(t2, notNullValue());
        assertThat(t1.size(), equalTo(2));

        Optional<MetadataDto> firstActual = t1.stream().findFirst();
        assertThat(firstActual.isPresent(), equalTo(true));

        Optional<ItemMetadata> firstExpected = t2.stream().filter(m -> m.getName().equals(firstActual.get().getName())).findFirst();
        assertThat(firstExpected.isPresent(), equalTo(true));

        assertThat(firstActual.get().getName(), equalTo(firstExpected.get().getName()));
        assertThat(firstActual.get().getValue(), equalTo(firstExpected.get().getValue()));
    }

    private static void verifyStatusTransitionSetMatch(StatusTransitionDto st1, StatusTransition st2) {
        assertThat(st1, notNullValue());
        assertThat(st2, notNullValue());

        assertThat(st1.getOccurred(), equalTo(st2.getOccurred()));
        assertThat(st1.getTo(), equalTo(st2.getTo().getCode()));
        assertThat(st1.getFrom(), equalTo(st2.getFrom().getCode()));
    }
}