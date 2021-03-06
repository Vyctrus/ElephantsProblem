import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class EleksProblem {
    Map<String, String> previousOf = new HashMap<String, String>();
    Map<String, String> nextOf = new HashMap<String, String>();
    Map<String, Float> weightOf = new HashMap<String, Float>();
    String minAbs;

    List<List<String>> cycles;

    Map<String, String> previousOfM1 = new HashMap<String, String>();
    Map<String, String> nextOfM1 = new HashMap<String, String>();

    Map<String, String> previousOfM2 = new HashMap<String, String>();
    Map<String, String> nextOfM2 = new HashMap<String, String>();

    public static void main(String[] args) {
        EleksProblem ep = new EleksProblem();
        ep.mainProgram();
    }

    public void mainProgram() {
        readData();
        graphInCycles();
        separeteCyclesTest();

        showWeightsTest();

        calculateTheCost();
    }

    public String getMinimumOfCycle(List<String> cycle) {
        float minC = weightOf.get(cycle.get(0));
        String minimumOfCycleId = cycle.get(0);
        for (int j = 0; j < cycle.size(); j++) {
            if (weightOf.get(cycle.get(j)) < minC) {
                minC = weightOf.get(cycle.get(j));
                minimumOfCycleId = cycle.get(j);
            }
        }
        return minimumOfCycleId;
    }

    public float method1(int cycleId) {
        float method1Cost = 0;
        List<String> copiedCycle = new ArrayList<>(cycles.get(cycleId));
        Map<String, String> copiedNextOf = new HashMap<String, String>(nextOf);
        Map<String, String> copiedPreviousOf = new HashMap<String, String>(previousOf);
        String minimumOfCycleNumber = getMinimumOfCycle(copiedCycle);

        while (!Objects.equals(copiedNextOf.get(minimumOfCycleNumber), minimumOfCycleNumber)) {
            String previous = copiedPreviousOf.get(minimumOfCycleNumber);
            System.out.println("Previous: " + previous + " MinC: " + minimumOfCycleNumber);
            // 1/2
            copiedNextOf.put(copiedPreviousOf.get(previous), minimumOfCycleNumber);
            copiedPreviousOf.put(minimumOfCycleNumber, copiedPreviousOf.get(previous));
            // 2/2
            copiedNextOf.put(previous, previous);
            copiedPreviousOf.put(previous, previous);

            method1Cost += weightOf.get(previous);
            method1Cost += weightOf.get(minimumOfCycleNumber);
        }
        previousOfM1 = copiedPreviousOf;
        nextOfM1 = copiedNextOf;
        if (method1Cost == method1Checker(copiedCycle, minimumOfCycleNumber)) {

        } else {
            throw new IllegalStateException("method1Cost is calculated wrong");
        }
        return method1Cost;
    }

    public float method1Checker(List<String> copiedCycle, String minC) {
        float sum = 0;
        for (String cycleId : copiedCycle) {
            sum += weightOf.get(cycleId);
        }
        float more = (copiedCycle.size() - 2) * weightOf.get(minC);
        return sum + more;
    }

    public float method2Checker(List<String> copiedCycle, String minC) {
        float sum = 0;
        for (String cycleId : copiedCycle) {
            sum += weightOf.get(cycleId);
        }
        float more = weightOf.get(minC) + (copiedCycle.size() + 1) * weightOf.get(minAbs);
        return sum + more;
    }

    public float method2(int cycleId) {
        float method2Cost = 0;
        List<String> copiedCycle = new ArrayList<>(cycles.get(cycleId));
        Map<String, String> copiedNextOf = new HashMap<String, String>(nextOf);
        Map<String, String> copiedPreviousOf = new HashMap<String, String>(previousOf);
        String minimumOfCycleNumber = getMinimumOfCycle(copiedCycle);

        if (weightOf.get(minimumOfCycleNumber) <= weightOf.get(minAbs) || copiedCycle.size() == 1) {
            // there is no point to swap minAbs with minC
            if (weightOf.get(minimumOfCycleNumber) < weightOf.get(minAbs)) {
                new Exception("Imposible, check code for errors. Min Abs should be the smallest");
            }
            System.out.println("Use of method1.(in m2).. ");
            method2Cost = method1(cycleId);
            previousOfM2 = previousOfM1;
            nextOfM2 = nextOfM1;
            return method2Cost;
        } else {
            // save data before swap
            String savePreviousOfMinAbs = copiedPreviousOf.get(minAbs);
            String saveNextOfMinAbs = copiedNextOf.get(minAbs);

            // swap data minC and minAbs
            method2Cost += weightOf.get(minAbs);
            method2Cost += weightOf.get(minimumOfCycleNumber);
            ChangeElement(minimumOfCycleNumber, minAbs, copiedNextOf, copiedPreviousOf);

            // do the thing
            while (!Objects.equals(copiedNextOf.get(minAbs), minAbs)) {
                System.out.println("While in method2... ");
                String previous = copiedPreviousOf.get(minAbs);
                System.out.println("Previous: " + previous + " MinC: " + minAbs);
                // 1/2
                copiedNextOf.put(copiedPreviousOf.get(previous), minAbs);
                copiedPreviousOf.put(minAbs, copiedPreviousOf.get(previous));
                // 2/2
                copiedNextOf.put(previous, previous);
                copiedPreviousOf.put(previous, previous);

                method2Cost += weightOf.get(previous);
                method2Cost += weightOf.get(minAbs);
            }
            // restore data after swap
            method2Cost += weightOf.get(minAbs);
            method2Cost += weightOf.get(minimumOfCycleNumber);

            // A-> X ->C
            RestoreElement(minAbs, savePreviousOfMinAbs, saveNextOfMinAbs, copiedNextOf, copiedPreviousOf);

            previousOfM2 = copiedPreviousOf;
            nextOfM2 = copiedNextOf;

            if (method2Cost == method1Checker(copiedCycle, minimumOfCycleNumber)) {

            } else {
                throw new IllegalStateException("method1Cost is calculated wrong");
            }

            return method2Cost;
        }
    }

    public void setAsNext(String elemX, String elemY, Map<String, String> copiedNextOf,
            Map<String, String> copiedPreviousOf) {
        // sets Y as next element of X, X->Y
        copiedNextOf.put(elemX, elemY);
        copiedPreviousOf.put(elemY, elemX);
    }

    public void setAsPrevious(String elemX, String elemY, Map<String, String> copiedNextOf,
            Map<String, String> copiedPreviousOf) {
        // sets Y as previous element of X, Y->X
        copiedPreviousOf.put(elemX, elemY);
        copiedNextOf.put(elemY, elemX);
    }

    public void RestoreElement(String elementMinAbs, String savedPrevious, String savedNext,
            Map<String, String> copiedNextOf,
            Map<String, String> copiedPreviousOf) {
        // Connect element minAbs to his old values

        // connection Previous->minAbs
        setAsNext(savedPrevious, elementMinAbs, copiedNextOf, copiedPreviousOf);

        // copiedNextOf.put(savedPrevious, elementMinAbs);
        // copiedPreviousOf.put(elementMinAbs, savedPrevious);

        // conection minAbs->Next
        setAsNext(elementMinAbs, savedNext, copiedNextOf, copiedPreviousOf);

        // copiedNextOf.put(elementMinAbs, savedNext);
        // copiedPreviousOf.put(savedNext, elementMinAbs);

    }

    public void ChangeElement(String minC, String minAbs, Map<String, String> copiedNextOf,
            Map<String, String> copiedPreviousOf) {
        // C->B->A change X to Y
        // String elementA = copiedNextOf.get(elementX);
        // String elementC = copiedPreviousOf.get(elementX);

        // copiedPreviousOf.put(elementA, elementY);
        // copiedNextOf.put(elementY, elementA);

        // copiedPreviousOf.put(elementY, elementC);
        // copiedNextOf.put(elementC, elementY);
        String previous = copiedPreviousOf.get(minC);
        String next = copiedNextOf.get(minC);

        setAsNext(minAbs, next, copiedNextOf, copiedPreviousOf);
        setAsNext(previous, minAbs, copiedNextOf, copiedPreviousOf);
    }

    public void calculateTheCost() {
        float totalCost = 0;
        for (int i = 0; i < cycles.size(); i++) {
            System.out.println("Analiza cyklu o id: " + i);
            float method1Cost = method1(i);
            System.out.println("Koncze metode1, koszt: " + method1Cost);
            float method2Cost = method2(i);
            System.out.println("Koncze metode2, koszt: " + method2Cost);
            System.out.print("\n");
            if (method1Cost <= method2Cost) {
                totalCost += method1Cost;
                // change graph accordingly
                nextOf = nextOfM1;
                previousOf = previousOfM1;
            } else {
                totalCost += method2Cost;
                // change graph accordingly
                nextOf = nextOfM2;
                previousOf = previousOfM2;
            }
        }
        System.out.println("Ostateczny wynik to pamparampamp!!!: " + totalCost);
    }

    public void separeteCyclesTest() {
        // test: eqpected cycles printed in separete lines
        System.out.println("Cycles size: " + cycles.size());
        for (var item : cycles) {
            for (var elephant : item) {
                System.out.print(" " + elephant);
            }
            System.out.print("\n");
        }
    }

    public void showWeightsTest() {
        System.out.println("Weights:");
        for (Map.Entry<String, Float> entry : weightOf.entrySet()) {
            String key = entry.getKey();
            float value = entry.getValue();
            System.out.print(" [" + key + "]" + " = " + value);
        }
        System.out.print("\n");
    }

    public void graphInCycles() {
        Map<String, Boolean> considered = new HashMap<String, Boolean>();
        for (Map.Entry<String, String> entry : nextOf.entrySet()) {
            String key = entry.getKey();
            considered.put(key, false);
        }
        cycles = new ArrayList<List<String>>();
        for (Map.Entry<String, String> entry : nextOf.entrySet()) {
            String key = entry.getKey();
            String startPoint = key;
            if (!considered.get(key)) {
                considered.put(key, true);
                List<String> singleCycle = new ArrayList<>();
                singleCycle.add(startPoint);
                String currentPoint = nextOf.get(startPoint);
                while (true) {
                    if (startPoint.equals(currentPoint)) {
                        // endOfCycle
                        cycles.add(singleCycle);
                        break;
                    } else {
                        singleCycle.add(currentPoint);
                        considered.put(currentPoint, true);
                        currentPoint = nextOf.get(currentPoint);
                    }
                }
            }
        }
    }

    public void readData() {
        Scanner stdInput;
        int howMany;
        float[] elekSizes;
        int[] elekA;
        int[] elekB;
        stdInput = new Scanner(System.in); // Create a Scanner object
        howMany = Integer.parseInt(stdInput.nextLine()); // Read user input
        System.out.println("Elek sizes");
        elekSizes = loadSizeArray(howMany, stdInput);
        System.out.println("Elek positions");
        elekA = loadIntArray(howMany, stdInput);
        System.out.println("Elek desired positions");
        elekB = loadIntArray(howMany, stdInput);
        System.out.println("Loading data comlpeted");
        createUsefulStructures(elekA, elekB, elekSizes);
    }

    public void createUsefulStructures(int[] elekA, int[] elekB, float[] elekSizes) {
        for (int i = 0; i < elekA.length; i++) {
            nextOf.put(Integer.toString(elekB[i]), Integer.toString(elekA[i])); // slon o numerze x, ma zjac miejsce
                                                                                // tego slonia co ma index y
        }
        for (Map.Entry<String, String> entry : nextOf.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            previousOf.put(value, key);
        }
        for (int i = 0; i < elekA.length; i++) {
            weightOf.put(Integer.toString(elekA[i]), elekSizes[i]);
        }
        minAbs = numberOfSmallElek(elekSizes);
    }

    public static int[] loadIntArray(int size, Scanner stdInput) {
        String stdLine = stdInput.nextLine();
        StringTokenizer st = new StringTokenizer(stdLine, " ");
        int[] resultArray = new int[size];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = Integer.parseInt(st.nextToken());// - 1;// java indexing starts at 0, easier to maintain
        }
        return resultArray;
    }

    public static float[] loadSizeArray(int size, Scanner stdInput) {
        String stdLine = stdInput.nextLine();
        StringTokenizer st = new StringTokenizer(stdLine, " ");
        float[] resultArray = new float[size];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = Float.parseFloat(st.nextToken());
        }
        return resultArray;
    }

    public String numberOfSmallElek(float[] elekSizes) {
        float minWeight = elekSizes[0];
        String minWeightIndex = "0";
        for (Map.Entry<String, String> entry : nextOf.entrySet()) {
            String key = entry.getKey();
            if (weightOf.get(key) <= minWeight) {
                minWeight = weightOf.get(key);
                minWeightIndex = key;
            }
        }
        return minWeightIndex;
    }

}
