/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

public class Classifications {
    //
    // new PitfallCategroy(new PitfallCategroyId('H',0),null,null);new PitfallCategroy(new PitfallCategroyId('C',1),new
    // PitfallCategroyId('H',0),"Classification by Dimension");new PitfallCategroy(new PitfallCategroyId('S',1),new
    // PitfallCategroyId('C',1),"Structural Dimension");new PitfallCategroy(new PitfallCategroyId('N',1),new
    // PitfallCategroyId('S',1),"Modelling Decisions");new PitfallCategroy(new PitfallCategroyId('N',2),new
    // PitfallCategroyId('S',1),"Wrong Inference");new PitfallCategroy(new PitfallCategroyId('N',3),new
    // PitfallCategroyId('S',1),"No Inference");new PitfallCategroy(new PitfallCategroyId('N',41),new
    // PitfallCategroyId('S',1),"Ontology language");new PitfallCategroy(new PitfallCategroyId('S',2),new
    // PitfallCategroyId('C',1),"Functional Dimension");new PitfallCategroy(new PitfallCategroyId('N',4),new
    // PitfallCategroyId('S',2),"Real World Modelling or Common Sense");new PitfallCategroy(new
    // PitfallCategroyId('N',5),new PitfallCategroyId('S',2),"Requirements Completeness");new PitfallCategroy(new
    // PitfallCategroyId('N',51),new PitfallCategroyId('S',2),"Application context");new PitfallCategroy(new
    // PitfallCategroyId('S',3),new PitfallCategroyId('C',1),"Usability-Profiling Dimension");new PitfallCategroy(new
    // PitfallCategroyId('N',6),new PitfallCategroyId('S',3),"Ontology Clarity");new PitfallCategroy(new
    // PitfallCategroyId('N',7),new PitfallCategroyId('S',3),"Ontology Understanding");new PitfallCategroy(new
    // PitfallCategroyId('N',8),new PitfallCategroyId('S',3),"Ontology Metadata");new PitfallCategroy(new
    // PitfallCategroyId('C',2),new PitfallCategroyId('H',0),"Classification by Evaluation Criteria");new
    // PitfallCategroy(new PitfallCategroyId('S',4),new PitfallCategroyId('C',2),"Consistency");new PitfallCategroy(new
    // PitfallCategroyId('S',5),new PitfallCategroyId('C',2),"Completeness");new PitfallCategroy(new
    // PitfallCategroyId('S',6),new PitfallCategroyId('C',2),"Consciseness");
    //
    // 02,03,04,05,06,07,08,09,Set.of(new PitfallCategoryId('N',5),new
    // PitfallCategoryId('N',5)),10,11,12,13,14,Set.of(new PitfallCategoryId('N',1),new PitfallCategoryId('N',1),new
    // PitfallCategoryId('S',4)),15,Set.of(new PitfallCategoryId('S',4),new PitfallCategoryId('S',4)),16,Set.of(new
    // PitfallCategoryId('N',3),new PitfallCategoryId('N',3),new PitfallCategoryId('S',5)),17,Set.of(new
    // PitfallCategoryId('N',1),new PitfallCategoryId('N',1)),18,Set.of(new PitfallCategoryId('N',2),new
    // PitfallCategoryId('N',2),new PitfallCategoryId('S',4)),19,20,21,22,23,Set.of(new PitfallCategoryId('N',1),new
    // PitfallCategoryId('N',1)),24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,
    //
    // 41,
    //
    // String keys[] = {
    // "H0",
    // "C1",
    // "S1",
    // "N1",
    // "N2",
    // "N3",
    // "N41",
    // "S2",
    // "N4",
    // "N5",
    // "N51",
    // "S3",
    // "N6",
    // "N7",
    // "N8",
    // "C2",
    // "S4",
    // "S5",
    // "S6"
    // };
    //
    // String titles[] = { /* H0 */ null, /* C1 */ "Classification by Dimension", /* S1 */ "Structural Dimension",
    // /* N1 */ "Modelling Decisions", /* N2 */ "Wrong Inference", /* N3 */ "No Inference",
    // /* N41 */ "Ontology language", /* S2 */ "Functional Dimension",
    // /* N4 */ "Real World Modelling or Common Sense",
    // // movido aqui
    // /* N5 */ "Requirements Completeness", /* N51 */ "Application context",
    // /* S3 */ "Usability-Profiling Dimension", /* N6 */ "Ontology Clarity", /* N7 */ "Ontology Understanding",
    // /* N8 */ "Ontology Metadata", /* C2 */ "Classification by Evaluation Criteria", /* S4 */ "Consistency",
    // /* S5 */ "Completeness", /* S6 */ "Consciseness" };
    //
    // String parents[] = { /* H0 */ null, /* C1 */ "H0", /* S1 */ "C1", /* N1 */ "S1", /* N2 */ "S1", /* N3 */ "S1",
    // /* N41 */ "S1", /* S2 */ "C1", /* N4 */ "S2", /* N5 */ "S2", /* N51 */ "S2", /* S3 */ "C1", /* N6 */ "S3",
    // /* N7 */ "S3", /* N8 */ "S3", /* C2 */ "H0", /* S4 */ "C2", /* S5 */ "C2", /* S6 */ "C2", /* END */ "END" };
    //
    // // String descriptions[] = { /* H0 */ null,
    // // /* C1 */ "",
    // // /* S1 */ "Checks for pitfalls: P02, P03, P07, P05, P06, P10, P11, P12, P13, P19, P21, P24, P25, P26, P27, P28,
    // // P29, P30, P31, P33, P34, P35 and P38.",
    // // /* N1 */ "Checks for pitfalls: P02, P03, P07, P21, P24, P25, P26 and P33.",
    // // /* N2 */ "Checks for pitfalls: P05, P06, P19, P27, P28, P29 and P31",
    // // /* N3 */ "Checks for pitfalls: P11, P12, P13 and P30.",
    // // /* N41 */ "Checks for pitfalls: P34, P35 and P38.",
    // // /* S2 */ "Checks for pitfalls: P04, P09, P10, P36, P37, P38, P39 and P40.",
    // // /* N4 */ "Checks for pitfall: P04 and P10.",
    // // /* N5 */ "Checks for pitfall: P04 and P09.",
    // // /* N51 */ "Checks for pitfalls: P36, P37, P38, P39 and P40.",
    // // /* S3 */ "Checks for pitfalls: P02, P07, P08, P11, P12, P13, P20, P22, P32, P37, P38 and P41.",
    // // /* N6 */ "Checks for pitfalls: P08 and P22.",
    // // /* N7 */ "Checks for pitfalls: P02, P07, P08, P11, P12, P13, P20, P32 and P37",
    // // /* N8 */ "Checks for pitfalls: P38 and P41",
    // // /* C2 */ "",
    // // /* S4 */ "Checks for pitfalls: P05, P06, P07, P19 and P24.",
    // // /* S5 */ "Checks for pitfalls: P04, P10, P11, P12 and P13.",
    // // /* S6 */ "Checks for pitfalls: P02, P03 and P21."
    // // };
    //
    // /* Modelling Decisions */
    // String PitsN1[] = { "P02", "P03", "P07", "P21", "P24", "P25", "P26", "P33" };
    // /*
    // * Missing "P01", "P14", "P17", "P23"
    // */
    //
    // /* Wrong Inference */
    // String PitsN2[] = { "P05", "P06", "P19", "P27", "P28", "P29", "P31" }; /*
    // * Missing "P15", "P18"
    // */
    //
    // /* No Inference */
    // String PitsN3[] = { "P11", "P12", "P13", "P30" }; /*
    // * Missing "P09", "P16"
    // */
    //
    // /* Ontology language */
    // String PitsN41[] = { "P34", "P35", "P38" };

    // /* Structural Dimension = N1 + N2 + N3 + N4 */
    // String PitsS1[] = MergeArrays.merge(PitsN1, PitsN2, PitsN3, PitsN41);

    // /* Real World Modelling or Common Sense */
    // String PitsN4[] = { "P04", "P10" }; /* Missing "P09" */
    //
    // /* Requirements Completeness */
    // String PitsN5[] = { "P04", "P09" }; /* Missing "P09" */
    //
    // /* Application context */
    // String PitsN51[] = { "P36", "P37", "P38", "P39", "P40" };

    // /* Functional Dimension */
    // String PitsS2[] = MergeArrays.merge(PitsN4, PitsN5, PitsN51);

    /* Ontology Clarity */
    // String PitsN6[] = { "P08", "P22" };
    //
    // /* Ontology Understanding */
    // String PitsN7[] = { "P02", "P07", "P08", "P11", "P12", "P13", "P20", "P32", "P37" }; /* Missing "P01" */
    //
    // /* Ontology Metadata */
    // String PitsN8[] = { "P38", "P41" };

    // /* Usability-Profiling Dimension = N6 + N7 */
    // String PitsS3[] = MergeArrays.merge(PitsN6, PitsN7, PitsN8);

    // /* Consistency */
    // String PitsS4[] = { "P05", "P06", "P07", "P19", "P24" }; /*
    // * Missing "P01", "P14", "P15", "P18"
    // */
    //
    // /* Completeness */
    // String PitsS5[] = { "P04", "P10", "P11", "P12", "P13" }; /*
    // * Missing "P09", "P16"
    // */
    //
    // /* Consciseness */
    // String PitsS6[] = { "P02", "P03", "P21" }; /* Missing "P17" */

    // public Classifications() {
    // }
    //
    // public String[] getKeys() {
    // return keys;
    // }
    //
    // public String[] getTitles() {
    // return titles;
    // }
    //
    // public String[] getDescriptions() {
    // return descriptions;
    // }
    //
    // public String[] getParents() {
    // return parents;
    // }
    //
    // public String[] getPits(String pit) {
    // if (pit.equalsIgnoreCase("N1")) {
    // return PitsN1;
    // } else if (pit.equalsIgnoreCase("N2")) {
    // return PitsN2;
    // } else if (pit.equalsIgnoreCase("N3")) {
    // return PitsN3;
    // } else if (pit.equalsIgnoreCase("N4")) {
    // return PitsN4;
    // } else if (pit.equalsIgnoreCase("N41")) {
    // return PitsN41;
    // } else if (pit.equalsIgnoreCase("N5")) {
    // return PitsN5;
    // } else if (pit.equalsIgnoreCase("N51")) {
    // return PitsN51;
    // } else if (pit.equalsIgnoreCase("N6")) {
    // return PitsN6;
    // } else if (pit.equalsIgnoreCase("N7")) {
    // return PitsN7;
    // } else if (pit.equalsIgnoreCase("S1")) {
    // return PitsS1;
    // } else if (pit.equalsIgnoreCase("S2")) {
    // return PitsS2;
    // } else if (pit.equalsIgnoreCase("S3")) {
    // return PitsS3;
    // } else if (pit.equalsIgnoreCase("S4")) {
    // return PitsS4;
    // } else if (pit.equalsIgnoreCase("S5")) {
    // return PitsS5;
    // } else {
    // return PitsS6;
    // }
    // }
}