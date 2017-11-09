package ch.elexis.views;

import java.util.HashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;

import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.data.Query;
import ch.elexis.data.TarmedLeistung;

public class TarmedCodeSelectorContentProvider
		implements ICommonViewerContentProvider, ITreeContentProvider {
	
	private Query<TarmedLeistung> rootQuery;
	private Query<TarmedLeistung> childrenQuery;
	
	private CommonViewer commonViewer;
	
	private boolean isFiltered;
	private String queryZiffer;
	private String queryText;
	
	private String queryLaw;
	private String queryValidFrom;
	
	public TarmedCodeSelectorContentProvider(CommonViewer commonViewer){
		this.commonViewer = commonViewer;
		
		rootQuery = new Query<>(TarmedLeistung.class);
		childrenQuery = new Query<>(TarmedLeistung.class);
	}
	
	@Override
	public void changed(HashMap<String, String> values){
		this.queryZiffer = values.get("Ziffer");
		this.queryText = values.get("Text");
		
		if (shouldFilter()) {
			if (!isFiltered) {
				isFiltered = true;
			}
			commonViewer.getViewerWidget().getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run(){
					commonViewer.getViewerWidget().refresh();
				}
			});
		} else if (isFiltered) {
			isFiltered = false;
			commonViewer.getViewerWidget().getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run(){
					commonViewer.getViewerWidget().refresh();
				}
			});
		}
		
	}
	
	private boolean shouldFilter(){
		return queryZiffer.length() > 2 || queryText.length() > 2;
	}
	
	@Override
	public void reorder(String field){
		// TODO Auto-generated method stub
		System.out.println(field);
	}
	
	@Override
	public void selected(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void init(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void startListening(){
		commonViewer.getConfigurer().getControlFieldProvider().addChangeListener(this);
	}
	
	@Override
	public void stopListening(){
		commonViewer.getConfigurer().getControlFieldProvider().removeChangeListener(this);
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		rootQuery.clear();
		rootQuery.add(TarmedLeistung.FLD_PARENT, Query.EQUALS, "NIL");
		rootQuery.orderBy(false, TarmedLeistung.FLD_CODE);
		return rootQuery.execute().toArray();
	}
	
	@Override
	public Object[] getChildren(Object parentElement){
		if (parentElement instanceof TarmedLeistung) {
			TarmedLeistung parentLeistung = (TarmedLeistung) parentElement;
			childrenQuery.clear();
			childrenQuery.add(TarmedLeistung.FLD_PARENT, Query.EQUALS, parentLeistung.getId());
			childrenQuery.orderBy(false, TarmedLeistung.FLD_CODE);
			return childrenQuery.execute().toArray();
		}
		return null;
	}
	
	@Override
	public Object getParent(Object element){
		if (element instanceof TarmedLeistung) {
			TarmedLeistung leistung = (TarmedLeistung) element;
			return TarmedLeistung.load(leistung.get(TarmedLeistung.FLD_PARENT));
		}
		return null;
	}
	
	@Override
	public boolean hasChildren(Object parentElement){
		if (parentElement instanceof TarmedLeistung) {
			TarmedLeistung parentLeistung = (TarmedLeistung) parentElement;
			childrenQuery.clear();
			childrenQuery.add(TarmedLeistung.FLD_PARENT, Query.EQUALS, parentLeistung.getId());
			return !childrenQuery.execute().isEmpty();
		}
		return false;
	}
}
