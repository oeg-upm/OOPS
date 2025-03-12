/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CheckersCatalogue {

    private static ServiceLoader<Checker> LOADER_CHECKERS = ServiceLoader.load(Checker.class);

    public static final List<CheckerInfo> CHECKER_INFOS_UNIMPLEMENTED = Arrays.asList(new CheckerInfo(new CheckerId(1),
            Set.of(new PitfallCategoryId('N', 1), new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 4)),
            Importance.CRITICAL, "Creating polysemous elements",
            "Creating polysemous elements:\n"
                    + "An ontology element (class, object property or datatype property) whose identifier has different senses "
                    + "is included in the ontology to represent more than one conceptual idea or property.",
            Arity.ONE),
            new CheckerInfo(new CheckerId(9), Set.of(new PitfallCategoryId('N', 5)), Importance.MINOR,
                    "Missing domain information",
                    "Part of the information needed for modeling the intended domain is not included in the ontology.\n"
                            + "\n" + "This pitfall may be related to\n"
                            + "(a) the requirements included in the Ontology Requirement Specification Document (ORSD) that are not covered by the ontology, or\n"
                            + "(b) to the lack of knowledge that can be added to the ontology to make it more complete.",
                    Arity.ZERO),
            new CheckerInfo(new CheckerId(14), Set.of(new PitfallCategoryId('N', 1), new PitfallCategoryId('S', 4)),
                    Importance.CRITICAL, "Misusing \"owl:allValuesFrom\"",
                    "This pitfall consists in using the universal restriction (owl:allValuesFrom) as the default qualifier "
                            + "instead of the existential restriction (owl:someValuesFrom).\n" + "\n"
                            + "Additional information about this pitfall is provided in [7].",
                    Arity.ONE),
            new CheckerInfo(new CheckerId(15), Set.of(new PitfallCategoryId('S', 4)), Importance.CRITICAL,
                    "Using \"some not\" in place of \"not some\"",
                    "The pitfall consists in using a \"some not\" structure when a \"not some\" is required. "
                            + "This is due to the misplacement of the existential quantifier (owl:someValuesFrom) "
                            + "and the negative operator (owl:complementOf).\n"
                            + "(a) When to use a \"some not\" structure (DrelationshipS: ClassA): "
                            + "to state that there is at least one individual acting as object of the relationship \"relationshipS\" "
                            + "and such individual do not belong to class \"ClassA\". "
                            + "This implies that there must be at least one instantiation of the relationshipsS whose target does not belong to \"ClassA\". "
                            + "This does not prevent instances from ClassA acting as objects of the relationships.\n"
                            + "(b) When to use a \"not some\" structure ( DrelationshipS:ClassA): "
                            + "to state that no individuals in class \"ClassA\" act as objects of the relationship \"relationshipS\". "
                            + "This does not imply the existence of individuals from other classes acting as objects of the relationship.\n"
                            + "\n" + "This pitfall is explained in more detail in [7].",
                    Arity.ONE), // TODO Check if this arity is correct
            new CheckerInfo(new CheckerId(16), Set.of(new PitfallCategoryId('N', 3), new PitfallCategoryId('S', 5)),
                    Importance.CRITICAL, "Using a primitive class in place of a defined one",
                    "\"Primitive\" classes are those for which there are only necessary conditions [7]. "
                            + "They are described using rdfs:subClassOf. "
                            + "\"Defined\" classes are those for which there are necessary and sufficient conditions [7]. "
                            + "They are described using owl:equivalentClass. "
                            + "This pitfall implies creating a primitive class rather than a defined one "
                            + "in case automatic classification of individuals is intended. "
                            + "It should be clarified that, in general, "
                            + "nothing will be inferred to be subsumed under a primitive class by the classifier [7].\n"
                            + "\n" + "This pitfall is related to the open world assumption.",
                    Arity.ONE),
            new CheckerInfo(new CheckerId(17), Set.of(new PitfallCategoryId('N', 1)), Importance.IMPORTANT,
                    "Overspecializing a hierarchy",
                    "The hierarchy in the ontology is specialized in such a way "
                            + "that the final leaves are defined as classes and these classes will not have instances.\n"
                            + "\n"
                            + "Authors in [e] provide guidelines for distinguishing between a class and an instance when modeling hierarchies.",
                    Arity.ONE), // TODO Check if this arity is correct
            new CheckerInfo(new CheckerId(18), Set.of(new PitfallCategoryId('N', 2), new PitfallCategoryId('S', 4)),
                    Importance.IMPORTANT, "Overspecializing the domain or range",
                    "This pitfall consists in defining a domain or range not general enough for a property, "
                            + "i.e no considering all the individuals or datatypes that might be involved in such a domain or range.\n"
                            + "\n" + "This pitfall is related to the guidelines provided in [2] and [7].",
                    Arity.ONE),
            new CheckerInfo(new CheckerId(23), Set.of(new PitfallCategoryId('N', 1)), Importance.IMPORTANT,
                    "Duplicating a datatype already provided by the implementation language",
                    "A class and its corresponding individuals are created to represent existing datatypes in the implementation language.",
                    Arity.ONE));

    private static List<PitfallInfo> PF_INFO = null;
    private static Map<PitfallId, PitfallInfo> PF_INFO_MAP = null;

    private CheckersCatalogue() {
    }

    public static List<Checker> getAllCheckers() {
        return LOADER_CHECKERS.stream().map((provider) -> provider.get()).collect(Collectors.toList());
    }

    public static List<Checker> getCheckers(final List<CheckerId> checkerIds) {
        return LOADER_CHECKERS.stream().map((checkerProvider) -> checkerProvider.get())
                .filter((checker) -> checkerIds.contains(checker.getInfo().getId())).collect(Collectors.toList());
    }

    public static List<CheckerInfo> getCheckerInfosImplemented() {
        return LOADER_CHECKERS.stream().map((checkerProvider) -> checkerProvider.get().getInfo())
                .collect(Collectors.toList());
    }

    public static List<CheckerInfo> getCheckerInfosUnimplemented() {
        return CHECKER_INFOS_UNIMPLEMENTED;
    }

    public static List<CheckerInfo> getCheckerInfosAll() {
        final List<CheckerInfo> infos = getCheckerInfosImplemented();
        infos.addAll(getCheckerInfosUnimplemented());
        return infos;
    }

    public static List<PitfallInfo> getPitfallInfosImplemented() {
        return LOADER_CHECKERS.stream().map((checkerProvider) -> checkerProvider.get().getInfo().detectsPitfalls())
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<PitfallInfo> getPitfallInfosUnimplemented() {
        return CHECKER_INFOS_UNIMPLEMENTED.stream().map((checkerInfo) -> checkerInfo.detectsPitfalls())
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<PitfallInfo> getPitfallInfosAll() {

        if (PF_INFO == null) {
            final List<PitfallInfo> info = getPitfallInfosImplemented();
            info.addAll(getPitfallInfosUnimplemented());
            PF_INFO = Collections.unmodifiableList(info);
            PF_INFO_MAP = PF_INFO.stream()
                    .collect(Collectors.toUnmodifiableMap(PitfallInfo::getId, Function.identity()));
        }
        return PF_INFO;
    }

    public static Map<PitfallId, PitfallInfo> getPitfallInfosAllMap() {

        if (PF_INFO_MAP == null) {
            PF_INFO_MAP = getPitfallInfosAll().stream()
                    .collect(Collectors.toUnmodifiableMap(PitfallInfo::getId, Function.identity()));
        }
        return PF_INFO_MAP;
    }

    public static PitfallInfo getPitfallInfo(final PitfallId pfId) {
        return getPitfallInfosAllMap().get(pfId);
    }
}
