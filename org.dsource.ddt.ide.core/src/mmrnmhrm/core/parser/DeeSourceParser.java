package mmrnmhrm.core.parser;

import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.dltk.core.IModelElement;

import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.ParserError;
import dtool.project.DeeNamingRules;

public class DeeSourceParser extends AbstractSourceParser {
	
	@Override
	public DeeModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
		DeeParserResult deeParserResult = parseToDeeParseResult(input, reporter);
		DeeModuleDeclaration deeModuleDecl = new DeeModuleDeclaration(deeParserResult);
		return deeModuleDecl;
	}
	
	public final String[] NOSTRINGS = new String[0];
	
	public DeeParserResult parseToDeeParseResult(IModuleSource input, IProblemReporter reporter) {
		String source = input.getSourceContents();
		
		String defaultModuleName = "_unnamedSource_";
		IModelElement modelElement = input.getModelElement();
		if(modelElement != null) {
			defaultModuleName = DeeNamingRules.getModuleNameFromFileName(modelElement.getElementName());
		}
		DeeParserResult deeParserResult = DeeParser.parseSource(source, defaultModuleName);
		
		if(reporter != null) {
			for (ParserError parserError : deeParserResult.errors) {
				reporter.reportProblem(new DefaultProblem(
					parserError.getUserMessage(),
					DefaultProblemIdentifier.decode(org.eclipse.dltk.compiler.problem.IProblem.Syntax),
					NOSTRINGS, 
					ProblemSeverities.Error,
					parserError.getStartPos(),
					parserError.getEndPos(),
					0 //TODO: review if we actually need end line
					)
				);
			}
		}
		
		return deeParserResult;
	}
	
}