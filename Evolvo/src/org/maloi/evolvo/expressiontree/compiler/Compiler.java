package org.maloi.evolvo.expressiontree.compiler;

import java.util.HashMap;
import java.util.Random;

import org.apache.bcel.Constants;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;
import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.operators.OperatorList;
import org.maloi.evolvo.expressiontree.vm.Instruction;
import org.maloi.evolvo.expressiontree.vm.Machine;
import org.maloi.evolvo.expressiontree.vm.Stack;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Compiler implements Constants
{
   static InstructionFactory _factory;
   static String classname;
   static Method[] opMethods;
   static HashMap opMethodMap;

   static {
      buildOperatorTable();
   }

   static public Object compile(Machine ma)
   {
      ClassGen cg;
      ConstantPoolGen cp;
      InstructionList il;
      MethodGen mg;
      int pc;
      int plen;
      Instruction[] program;

      plen = ma.getSize();
      program = ma.getProgram();

      classname = genClassName();

      cg =
         new ClassGen(
            classname,
            "java.lang.Object",
            "<generated>",
            ACC_PUBLIC | ACC_SUPER,
            null);

      cp = cg.getConstantPool();
      il = new InstructionList();
      _factory = new InstructionFactory(cg, cp);

      createFields(cg, cp);
		createSetRegistersMethod(cg, cp);      

      // here's where we do the actual compilation
      for (pc = 0; pc < plen; pc++)
      {
         switch (program[pc].type)
         {
            case Instruction.TYPE_OPERATOR :
               // add operator's perform method to our code
               //program[pc].op.perform(programStack);
               Method theMethod =
                  (Method)opMethodMap.get(program[pc].op.getName());

               Code code = theMethod.getCode();

               InstructionList nIL = new InstructionList(code.getCode());

               il.append(nIL);

               break;
            case Instruction.TYPE_REGISTER :
               // add a call to theStack.push
               //programStack.push(registers[program[pc].reg]);

               break;
            case Instruction.TYPE_VALUE :
               //programStack.push(program[pc].value);
               break;
         }
      }

      mg =
         new MethodGen(
            ACC_STATIC | ACC_PUBLIC,
            Type.VOID,
            new Type[] { Type.getType(Stack.class)},
            new String[] { "theStack" },
            "perform",
            classname,
            il,
            cp);

      return null;
   }

   private static void buildOperatorTable()
   {
      OperatorInterface[] ops = OperatorList.getAllOperators();
      Method methods[];
      Method theMethod;
      JavaClass clazz;

      opMethods = new Method[ops.length];
      opMethodMap = new HashMap();

      for (int i = 0; i < ops.length; i++)
      {
         Object op = ops[i];
         clazz = Repository.lookupClass(op.getClass());
         methods = clazz.getMethods();
         theMethod = null;
         for (int j = 0; j < methods.length; j++)
         {
            if (methods[i]
               .toString()
               .compareTo("public void perform(org.maloi.evolvo.expressiontree.vm.Stack theStack)")
               == 0)
            {
               theMethod = methods[i];
               break;
            }
         }

         opMethods[i] = theMethod;
         opMethodMap.put(((OperatorInterface)op).getName(), theMethod);
      }
   }

   /**
    * genClassName()
    * 
    * Generates a pseudorandom class name
    * 
    * @return the generated classname
    */
   static private String genClassName()
   {
      Random r;
      String acharS = "_ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
      char[] achar = acharS.toCharArray();
      char[] cN = new char[16];
      int cl = achar.length;

      cN[0] = 'e';
      cN[1] = 'v';
      cN[2] = 'o';
      cN[3] = 'l';
      cN[4] = 'v';
      cN[5] = 'o';

      r = new Random();

      for (int i = 6; i < 16; i++)
      {
         cN[i] = achar[r.nextInt(cl)];
      }

      return new String(cN);
   }

   /**
    * Creates the method header -- this involves such things
    * as initializing our register array.
    * 
    * @param cg
    * @param cp
    * @param il
    */
   private static void createMethodHeader(
      ClassGen cg,
      ConstantPoolGen cp,
      InstructionList il)
   {
      il.append(new PUSH(cp, Machine.FREE_REGISTER_BASE));
      il.append(
         _factory.createNewArray(new ArrayType(Type.DOUBLE, 1), (short)1));
      il.append(
         _factory.createFieldAccess(
            classname,
            "registers",
            new ArrayType(Type.DOUBLE, 1),
            Constants.PUTFIELD));
      InstructionHandle ih_18 =
         il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
      il.append(InstructionConstants.DUP);
   }

   private static void createSetRegistersMethod(
      ClassGen cg,
      ConstantPoolGen cp)
   {
   	InstructionList il = new InstructionList();
   	
      MethodGen method =
         new MethodGen(
            ACC_PUBLIC,
            Type.VOID,
            new Type[] { Type.INT, Type.DOUBLE },
            new String[] { "arg0", "arg1" },
            "setRegister",
            "org.maloi.evolvo.expressiontree.vm.Machine",
            il,
            cp);

      InstructionHandle ih_0 =
         il.append(InstructionFactory.createLoad(Type.INT, 1));
      il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
      il.append(
         _factory.createFieldAccess(
            "org.maloi.evolvo.expressiontree.vm.Machine",
            "registers",
            new ArrayType(Type.DOUBLE, 1),
            Constants.GETFIELD));
      il.append(InstructionConstants.ARRAYLENGTH);
      BranchInstruction if_icmple_6 =
         InstructionFactory.createBranchInstruction(Constants.IF_ICMPLE, null);
      il.append(if_icmple_6);
      InstructionHandle ih_9 =
         il.append(InstructionFactory.createReturn(Type.VOID));
      InstructionHandle ih_10 =
         il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
      il.append(
         _factory.createFieldAccess(
            "org.maloi.evolvo.expressiontree.vm.Machine",
            "registers",
            new ArrayType(Type.DOUBLE, 1),
            Constants.GETFIELD));
      il.append(InstructionFactory.createLoad(Type.INT, 1));
      il.append(InstructionFactory.createLoad(Type.DOUBLE, 2));
      il.append(InstructionConstants.DASTORE);
      InstructionHandle ih_17 =
         il.append(InstructionFactory.createReturn(Type.VOID));
      if_icmple_6.setTarget(ih_10);
      method.setMaxStack();
      method.setMaxLocals();
      cg.addMethod(method.getMethod());
      il.dispose();
   }

   private static void createFields(ClassGen cg, ConstantPoolGen cp)
   {
      FieldGen field;

      field =
         new FieldGen(
            ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
            Type.INT,
            "REGISTER_X",
            cp);
      field.setInitValue(0);
      cg.addField(field.getField());

      field =
         new FieldGen(
            ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
            Type.INT,
            "REGISTER_Y",
            cp);
      field.setInitValue(1);
      cg.addField(field.getField());

      field =
         new FieldGen(
            ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
            Type.INT,
            "REGISTER_R",
            cp);
      field.setInitValue(2);
      cg.addField(field.getField());

      field =
         new FieldGen(
            ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
            Type.INT,
            "REGISTER_THETA",
            cp);
      field.setInitValue(3);
      cg.addField(field.getField());

      field =
         new FieldGen(
            ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
            Type.INT,
            "FREE_REGISTER_BASE",
            cp);
      field.setInitValue(4);
      cg.addField(field.getField());

      field = new FieldGen(0, new ArrayType(Type.DOUBLE, 1), "registers", cp);
      cg.addField(field.getField());
   }

}
