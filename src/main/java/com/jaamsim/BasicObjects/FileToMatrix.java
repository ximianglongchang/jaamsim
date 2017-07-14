/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2017 JaamSim Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaamsim.BasicObjects;

import java.net.URI;
import java.util.ArrayList;

import com.jaamsim.basicsim.ErrorException;
import com.jaamsim.input.ExpError;
import com.jaamsim.input.ExpEvaluator;
import com.jaamsim.input.ExpParser;
import com.jaamsim.input.ExpResult;
import com.jaamsim.input.FileInput;
import com.jaamsim.input.Output;
import com.jaamsim.input.ExpParser.Expression;

public class FileToMatrix extends FileToArray {

	ArrayList<ArrayList<ExpResult>> value;

	public FileToMatrix() {}

	@Override
	protected void setValueForURI(URI uri) {
		value = getMatrixForURI(uri);
	}

	@Override
	protected void clearValue() {
		value = null;
	}

	private ArrayList<ArrayList<ExpResult>> getMatrixForURI(URI uri) {
		ArrayList<ArrayList<String>> tokens = FileInput.getTokensFromURI(uri);
		ArrayList<ArrayList<ExpResult>> ret = new ArrayList<>(tokens.size());
		for (ArrayList<String> strRecord : tokens) {
			ArrayList<ExpResult> record = new ArrayList<>(strRecord.size());
			for (String str : strRecord) {
				try {
					ExpEvaluator.EntityParseContext pc = ExpEvaluator.getParseContext(this, str);
					Expression exp = ExpParser.parseExpression(pc, str);
					ExpResult res = ExpEvaluator.evaluateExpression(exp, 0.0d);
					record.add(res);
				}
				catch (ExpError e) {
					throw new ErrorException(this, e);
				}
			}
			ret.add(record);
		}
		return ret;
	}

	@Output(name = "Value",
	 description = "A matrix containing the data from the input file.",
	    sequence = 1)
	public ArrayList<ArrayList<ExpResult>> getValue(double simTime) {
		return value;
	}

}
