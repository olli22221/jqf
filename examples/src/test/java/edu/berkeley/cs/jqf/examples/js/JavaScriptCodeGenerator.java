/*
 * Copyright (c) 2017-2018 The Regents of the University of California
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.berkeley.cs.jqf.examples.js;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import edu.berkeley.cs.jqf.examples.common.AsciiStringGenerator;
import edu.berkeley.cs.jqf.fuzz.util.Config;
import edu.berkeley.cs.jqf.fuzz.util.ConfigJavascriptGenerator;
import edu.berkeley.cs.jqf.fuzz.util.SwarmZestGenerator;

import static java.lang.Math.*;

/**
 * @author Rohan Padhye
 */
public class JavaScriptCodeGenerator extends SwarmZestGenerator<String> {
    public JavaScriptCodeGenerator() {
        super(String.class);
        fill_LambdaTokens(new Random());

    }

    private GenerationStatus status;
    private static final int MAX_IDENTIFIERS = 100;
    private static final int MAX_EXPRESSION_DEPTH = 10;
    private static final int MAX_STATEMENT_DEPTH = 6;
    private static Set<String> identifiers;
    private int statementDepth;
    private int expressionDepth;
    private Config config;



    private static final List<String> UNARY_TOKENS = Arrays.asList("!", "++", "--", "~",
            "delete", "new", "typeof");

    private Map<String,Boolean> UNARY_TOKENS_ = new HashMap<String,Boolean>(){{
        put("!",false);put("++",false);put("--",false);put("~",false);put("delete",false);put("new",false);put("typeof",false);
    }};

    private static final List<String> BINARY_TOKENS = Arrays.asList("!=", "!==", "%", "%=", "&", "&&", "&=", "*", "*=", "+", "+=", ",",
            "-", "-=", "/", "/=", "<", "<<", ">>=", "<=", "=", "==", "===",
            ">", ">=", ">>", ">>>", ">>>=", "^", "^=", "|", "|=", "||",
            "in", "instanceof");

    private Map<String,Boolean> BINARY_TOKENS_ = new HashMap<>(){{
        put("!=",false);put("!==",false);put("%",false);put("%=",false);put("&",false);put("&&",false);put("&=",false);
        put("*",false);put("*=",false);put("+",false);put("+=",false);put(",",false);put("-",false);put("-=",false);
        put("/",false);put("/=",false);put("<",false);put("<<",false);put(">>=",false);put("<=",false);put("=",false);
        put("==",false);put("===",false);put(">",false);put(">=",false);put(">>",false);put(">>>",false);
        put(">>>=",false);put("^",false);put("^=",false);put("|",false);put("|=",false);put("||",false);put("in",false);put("instanceof",false);
    }};

    private final List<Function<SourceOfRandomness, String>> BUTCFPIA_Tokens = Arrays.asList(this::generateIndexNode, this::generatePropertyNode,
            this::generateArrowFunctionNode ,this::generateCallNode,this::generateFunctionNode,this::generateTernaryNode);
    private Map<Function<SourceOfRandomness, String>,Boolean> BUTCFPIA_Tokens_ = new HashMap<>();

    private final List<Function<SourceOfRandomness, String>> EBCRTVE_Tokens = Arrays.asList(this::generateBreakNode, this::generateContinueNode,
            this::generateReturnNode ,this::generateThrowNode ,this::generateVarNode ,this::generateEmptyNode );
    private Map<Function<SourceOfRandomness, String>,Boolean> EBCRTVE_Tokens_ = new HashMap<>();

    private final List<Function<SourceOfRandomness, String>> IFWNSTB_Tokens = Arrays.asList(this::generateIfNode, this::generateForNode,
            this::generateWhileNode ,this::generateSwitchNode,this::generateTryNode,this::generateBlock,this::generateNamedFunctionNode);
    private Map<Function<SourceOfRandomness, String>,Boolean> IFWNSTB_Tokens_ = new HashMap<>();

    private ArrayList<String> BinaryTokens = new ArrayList<>();
    public ArrayList<String> UnaryTokens = new ArrayList<>();
    public ArrayList<Function<SourceOfRandomness, String>> BUTCFPIANodes = new ArrayList<>();
    private ArrayList<Function<SourceOfRandomness, String>> EBCRTVENodes = new ArrayList<>();
    private ArrayList<Function<SourceOfRandomness, String>> IFWNSTBNodes = new ArrayList<>();

    @Override
    public void setConfig(Config config) {
         ConfigJavascriptGenerator config_ = (ConfigJavascriptGenerator) config;
        this.BUTCFPIA_Tokens_ = config_.BUTCFPIA_;
        this.IFWNSTB_Tokens_ = config_.IFWNSTB_;
        this.EBCRTVE_Tokens_ = config_.EBCRTVE_;
        this.BINARY_TOKENS_ = config_.BIN_;
        this.UNARY_TOKENS_ = config_.UNA_;
        fillFeatureSet();
    }

    @Override
    public Config getConfig() {
        return  new ConfigJavascriptGenerator(BUTCFPIA_Tokens_, EBCRTVE_Tokens_, IFWNSTB_Tokens_, BINARY_TOKENS_, UNARY_TOKENS_);

    }

    private void fill_LambdaTokens(Random random){
        BUTCFPIA_Tokens_.put(this::generateIndexNode,false);
        BUTCFPIA_Tokens_.put(this::generatePropertyNode,false);
        BUTCFPIA_Tokens_.put(this::generateArrowFunctionNode,false);
        BUTCFPIA_Tokens_.put(this::generateCallNode,false);
        BUTCFPIA_Tokens_.put(this::generateFunctionNode,false);
        BUTCFPIA_Tokens_.put(this::generateTernaryNode,false);

        EBCRTVE_Tokens_.put(this::generateBreakNode,false);
        EBCRTVE_Tokens_.put(this::generateContinueNode,false);
        EBCRTVE_Tokens_.put(this::generateReturnNode,false);
        EBCRTVE_Tokens_.put(this::generateThrowNode,false);
        EBCRTVE_Tokens_.put(this::generateVarNode,false);
        EBCRTVE_Tokens_.put(this::generateEmptyNode,false);

        IFWNSTB_Tokens_.put(this::generateIfNode,false);
        IFWNSTB_Tokens_.put(this::generateForNode,false);
        IFWNSTB_Tokens_.put(this::generateWhileNode,false);
        IFWNSTB_Tokens_.put(this::generateSwitchNode,false);
        IFWNSTB_Tokens_.put(this::generateTryNode,false);
        IFWNSTB_Tokens_.put(this::generateBlock,false);
        IFWNSTB_Tokens_.put(this::generateNamedFunctionNode,false);
        this.config = new ConfigJavascriptGenerator(BUTCFPIA_Tokens_, EBCRTVE_Tokens_, IFWNSTB_Tokens_, BINARY_TOKENS_, UNARY_TOKENS_);
        this.prepareFeatureSets(random);

    }


    private void emptyFeatureLists(){
        BinaryTokens.clear();
        UnaryTokens.clear();
        BUTCFPIANodes.clear();
        EBCRTVENodes.clear();
        IFWNSTBNodes.clear();
    }

    public static < T > T getRandomSetEntry(Set<T> set) {
        int size = set.size();
        int item = new Random().nextInt(size);
        int i = 0;
        for (T obj : set) {
            if (i == item)
                return obj;
            i++;
        }

        return null;
    }

    @Override
    public void updateFeatureSets(Random random, int number){


        boolean whichList;
        boolean whichList_;
        int whichList__;
        while (number > 0 ){

            whichList = random.nextBoolean();
            if (whichList) {

                whichList__ = random.nextInt(3)+1;
                if (whichList__ == 1){

                    Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry1 = getRandomSetEntry(EBCRTVE_Tokens_.entrySet());

                    if (entry1.getValue()){
                        EBCRTVENodes.remove(entry1.getKey());
                    }
                    else {
                        EBCRTVENodes.add(entry1.getKey());
                    }
                    entry1.setValue(!entry1.getValue());


                }
                else if(whichList__ == 2){
                    Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry2 = getRandomSetEntry(IFWNSTB_Tokens_.entrySet());
                    if (entry2.getValue()){
                        IFWNSTBNodes.remove(entry2.getKey());
                    }
                    else {
                        IFWNSTBNodes.add(entry2.getKey());
                    }
                    entry2.setValue(!entry2.getValue());
                }
                else {
                    Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry3 = getRandomSetEntry(BUTCFPIA_Tokens_.entrySet());
                    if (entry3.getValue()){
                        BUTCFPIANodes.remove(entry3.getKey());
                    }
                    else {
                        BUTCFPIANodes.add(entry3.getKey());
                    }
                    entry3.setValue(!entry3.getValue());
                }



            }
            else{
                whichList_=random.nextBoolean();
                if (whichList_) {

                    Map.Entry<String, Boolean> entry___ = getRandomSetEntry(UNARY_TOKENS_.entrySet());
                    if (entry___.getValue()) {

                        UnaryTokens.remove(entry___.getKey());
                        }
                    else{
                        UnaryTokens.add(entry___.getKey());
                    }
                    entry___.setValue(!entry___.getValue());

                    }

                else {
                    Map.Entry<String, Boolean> entry____ = getRandomSetEntry(BINARY_TOKENS_.entrySet());
                        if (entry____.getValue()){
                            BinaryTokens.remove(entry____.getKey());
                        }
                        else {
                            BinaryTokens.add(entry____.getKey());
                        }
                    entry____.setValue(!entry____.getValue());
                    }

                }
            number--;
            }



        }

    @Override
    public void setFeatureSets(){
        emptyFeatureLists();
        BUTCFPIANodes.add(this::generateBinaryNode);
        BUTCFPIANodes.add(this::generateUnaryNode);
        EBCRTVENodes.add(this::generateExpressionStatement);

        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : BUTCFPIA_Tokens_.entrySet()){
            if (entry.getValue()){
                BUTCFPIANodes.add(entry.getKey());

            }

        }
        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : EBCRTVE_Tokens_.entrySet()){
            if (entry.getValue()){
                EBCRTVENodes.add(entry.getKey());
            }

        }
        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : IFWNSTB_Tokens_.entrySet()){

            if (entry.getValue()){
                IFWNSTBNodes.add(entry.getKey());
            }

        }
        for (Map.Entry<String,Boolean> entry : UNARY_TOKENS_.entrySet()){
            if (entry.getValue()){
                UnaryTokens.add(entry.getKey());
            }

        }
        for (Map.Entry<String,Boolean> entry : BINARY_TOKENS_.entrySet()){
            if (entry.getValue()){
                BinaryTokens.add(entry.getKey());
            }

        }

    }


    private void fillFeatureSet(){
        emptyFeatureLists();
        BUTCFPIANodes.add(this::generateBinaryNode);
        BUTCFPIANodes.add(this::generateUnaryNode);
        EBCRTVENodes.add(this::generateExpressionStatement);

        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : BUTCFPIA_Tokens_.entrySet()){
            if (entry.getValue()){
                BUTCFPIANodes.add(entry.getKey());

            }
        }
        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : EBCRTVE_Tokens_.entrySet()){
            if (entry.getValue()){
                EBCRTVENodes.add(entry.getKey());
            }

        }
        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : IFWNSTB_Tokens_.entrySet()){

            if (entry.getValue()){
                IFWNSTBNodes.add(entry.getKey());

            }

        }
        for (Map.Entry<String,Boolean> entry : UNARY_TOKENS_.entrySet()){
            if (entry.getValue()){
                UnaryTokens.add(entry.getKey());
            }

        }
        for (Map.Entry<String,Boolean> entry : BINARY_TOKENS_.entrySet()){
            if (entry.getValue()){
                BinaryTokens.add(entry.getKey());

            }

        }

    }

    @Override
    public void prepareFeatureSets(Random random){
        emptyFeatureLists();
        BUTCFPIANodes.add(this::generateBinaryNode);
        BUTCFPIANodes.add(this::generateUnaryNode);
        EBCRTVENodes.add(this::generateExpressionStatement);

        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : BUTCFPIA_Tokens_.entrySet()){
            if (random.nextBoolean()){
                BUTCFPIANodes.add(entry.getKey());
                entry.setValue(true);
            }
            else{
                entry.setValue(false);
            }

        }
        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : EBCRTVE_Tokens_.entrySet()){
            if (random.nextBoolean()){
                EBCRTVENodes.add(entry.getKey());
                entry.setValue(true);
            }
            else{
                entry.setValue(false);
            }
        }
        for (Map.Entry<Function<SourceOfRandomness, String>,Boolean> entry : IFWNSTB_Tokens_.entrySet()){

            if (random.nextBoolean()){
                IFWNSTBNodes.add(entry.getKey());
                entry.setValue(true);
            }
            else{
                entry.setValue(false);
            }
        }
        for (Map.Entry<String,Boolean> entry : UNARY_TOKENS_.entrySet()){
            if (random.nextBoolean()){
                UnaryTokens.add(entry.getKey());
                entry.setValue(true);
            }
            else{
                entry.setValue(false);
            }
        }
        for (Map.Entry<String,Boolean> entry : BINARY_TOKENS_.entrySet()){
            if (random.nextBoolean()){
                BinaryTokens.add(entry.getKey());
                entry.setValue(true);
            }
            else{
                entry.setValue(false);
            }
        }


    }

    @Override
    public String generate(SourceOfRandomness random, GenerationStatus status) {

        this.status = status;
        this.identifiers = new HashSet<>();
        this.statementDepth = 0;
        this.expressionDepth = 0;


        return generateStatement(random).toString();
    }

    private static int sampleGeometric(SourceOfRandomness random, double mean) {
        double p = 1 / mean;
        double uniform = random.nextDouble();
        return (int) ceil(log(1 - uniform) / log(1 - p));
    }

    private static <T> List<T> generateItems(Function<SourceOfRandomness, T> generator, SourceOfRandomness random,
                                             double mean) {
        int len = sampleGeometric(random, mean);
        List<T> items = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            items.add(generator.apply(random));
        }
        return items;
    }

    private String generateExpression(SourceOfRandomness random) {
        expressionDepth++;
        // Choose between terminal or non-terminal
        String result;
        if (expressionDepth >= MAX_EXPRESSION_DEPTH || random.nextBoolean()) {
            result = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                    this::generateLiteralNode,
                    this::generateIdentNode
            )).apply(random);
        } else {
            result = random.choose(BUTCFPIANodes).apply(random);
        }
        expressionDepth--;
        return "(" + result + ")";
    }

    private String generateStatement(SourceOfRandomness random) {
        statementDepth++;
        String result;
        if (statementDepth >= MAX_STATEMENT_DEPTH || random.nextBoolean() || IFWNSTBNodes.size() == 0) {
            result = random.choose(EBCRTVENodes).apply(random);
        } else {
            result = random.choose(IFWNSTBNodes).apply(random);
        }
        statementDepth--;
        return result;
    }


    private String generateBinaryNode(SourceOfRandomness random) {
        String token;
        if (BinaryTokens.isEmpty()){
            token = random.choose(BINARY_TOKENS);
        }
        else {
            token = random.choose(BinaryTokens);
        }
        String lhs = generateExpression(random);
        String rhs = generateExpression(random);

        return lhs + " " + token + " " + rhs;
    }

    private String generateBlock(SourceOfRandomness random) {
        return "{ " + String.join(";", generateItems(this::generateStatement, random, 4)) + " }";
    }

    private String generateBlockStatement(SourceOfRandomness random) {
        return generateBlock(random);
    }

    private String generateBreakNode(SourceOfRandomness random) {
        return "break";
    }

    private String generateCallNode(SourceOfRandomness random) {
        String func = generateExpression(random);
        String args = String.join(",", generateItems(this::generateExpression, random, 3));

        String call = func + "(" + args + ")";
        if (random.nextBoolean()) {
            return call;
        } else {
            return "new " + call;
        }
    }

    private String generateCaseNode(SourceOfRandomness random) {
        return "case " + generateExpression(random) + ": " +  generateBlock(random);
    }

    private String generateCatchNode(SourceOfRandomness random) {
        return "catch (" + generateIdentNode(random) + ") " +
                generateBlock(random);
    }

    private String generateContinueNode(SourceOfRandomness random) {
        return "continue";
    }

    private String generateEmptyNode(SourceOfRandomness random) {
        return "";
    }

    private String generateExpressionStatement(SourceOfRandomness random) {
        return generateExpression(random);
    }

    private String generateForNode(SourceOfRandomness random) {
        String s = "for(";
        if (random.nextBoolean()) {
            s += generateExpression(random);
        }
        s += ";";
        if (random.nextBoolean()) {
            s += generateExpression(random);
        }
        s += ";";
        if (random.nextBoolean()) {
            s += generateExpression(random);
        }
        s += ")";
        s += generateBlock(random);
        return s;
    }

    private String generateFunctionNode(SourceOfRandomness random) {
        return "function(" + String.join(", ", generateItems(this::generateIdentNode, random, 5)) + ")" + generateBlock(random);
    }

    private String generateNamedFunctionNode(SourceOfRandomness random) {
        return "function " + generateIdentNode(random) + "(" + String.join(", ", generateItems(this::generateIdentNode, random, 5)) + ")" + generateBlock(random);
    }

    private String generateArrowFunctionNode(SourceOfRandomness random) {
        String params = "(" + String.join(", ", generateItems(this::generateIdentNode, random, 3)) + ")";
        if (random.nextBoolean()) {
            return params + " => " + generateBlock(random);
        } else {
            return params + " => " + generateExpression(random);
        }

    }

    private String generateIdentNode(SourceOfRandomness random) {
        // Either generate a new identifier or use an existing one
        String identifier;
        if (identifiers.isEmpty() || (identifiers.size() < MAX_IDENTIFIERS && random.nextBoolean())) {
            identifier = random.nextChar('a', 'z') + "_" + identifiers.size();
            identifiers.add(identifier);
        } else {
            identifier = random.choose(identifiers);
        }

        return identifier;
    }

    private String generateIfNode(SourceOfRandomness random) {
        return "if (" +
                generateExpression(random) + ") " +
                generateBlock(random) +
                (random.nextBoolean() ? generateBlock(random) : "");
    }

    private String generateIndexNode(SourceOfRandomness random) {
        return generateExpression(random) + "[" + generateExpression(random) + "]";
    }

    private String generateObjectProperty(SourceOfRandomness random) {
        return generateIdentNode(random) + ": " + generateExpression(random);
    }

    private String generateLiteralNode(SourceOfRandomness random) {
        if (expressionDepth < MAX_EXPRESSION_DEPTH && random.nextBoolean()) {
            if (random.nextBoolean()) {
                // Array literal
                return "[" + String.join(", ", generateItems(this::generateExpression, random, 3)) + "]";
            } else {
                // Object literal
                return "{" + String.join(", ", generateItems(this::generateObjectProperty, random, 3)) + "}";

            }
        } else {
            return random.choose(Arrays.<Supplier<String>>asList(
                    () -> String.valueOf(random.nextInt(-10, 1000)),
                    () -> String.valueOf(random.nextBoolean()),
                    () -> '"' + new AsciiStringGenerator().generate(random, status) + '"',
                    () -> "undefined",
                    () -> "null",
                    () -> "this"
            )).get();
        }
    }

    private String generatePropertyNode(SourceOfRandomness random) {
        return generateExpression(random) + "." + generateIdentNode(random);
    }

    private String generateReturnNode(SourceOfRandomness random) {
        return random.nextBoolean() ? "return" : "return " + generateExpression(random);
    }

    private String generateSwitchNode(SourceOfRandomness random) {
        return "switch(" + generateExpression(random) + ") {"
                + String.join(" ", generateItems(this::generateCaseNode, random, 2)) + "}";
    }

    private String generateTernaryNode(SourceOfRandomness random) {
        return generateExpression(random) + " ? " + generateExpression(random) +
                " : " + generateExpression(random);
    }

    private String generateThrowNode(SourceOfRandomness random) {
        return "throw " + generateExpression(random);
    }

    private String generateTryNode(SourceOfRandomness random) {
        return "try " + generateBlock(random) + generateCatchNode(random);
    }

    private String generateUnaryNode(SourceOfRandomness random) {
        String token;
        if (UnaryTokens.isEmpty()){
            token = random.choose(UNARY_TOKENS);
        }
        else{
            token = random.choose(UnaryTokens);
        }
        return token + " " + generateExpression(random);
    }

    private String generateVarNode(SourceOfRandomness random) {
        return "var " + generateIdentNode(random);
    }

    private String generateWhileNode(SourceOfRandomness random) {
        return "while (" + generateExpression(random) + ")" + generateBlock(random);
    }


}
