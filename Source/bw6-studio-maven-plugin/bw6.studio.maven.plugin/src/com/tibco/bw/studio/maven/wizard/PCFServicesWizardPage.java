package com.tibco.bw.studio.maven.wizard;

import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
import com.tibco.bw.studio.maven.modules.BWPCFModule;
import com.tibco.bw.studio.maven.modules.BWPCFServicesModule;
import com.tibco.bw.studio.maven.modules.BWProject;

public class PCFServicesWizardPage extends WizardPage{
	
	private BWProject project;
	private Composite container;
	private List<TableItem> appPCFServices=new ArrayList<TableItem>();
	private CloudFoundryClient client;
	private BWModule appmodule;
	List<CloudService> pcfServices=new ArrayList<CloudService>();
	private TableViewer tableViewer;
	
	protected PCFServicesWizardPage(String pageName , BWProject project ) {
		super(pageName);
		this.project = project;		 
		setTitle("PCF services selection :-");
		setDescription("Login to PCF instance, and select the required services for your application");	
		
	}

	@Override
	public void createControl(Composite parent) {

		for( BWModule module : project.getModules() )
		{
			if( module.getType() == BWModuleType.Application )
			{
				appmodule=module;
				break;
			}
		}
		
		container = new Composite(parent, SWT.BORDER);
		container.setLayout(new GridLayout(1, false));
		
        final Composite composite_1 = new Composite(container, SWT.NONE);
        composite_1.setLayout(new GridLayout(1, false));
        
		
		final Button loginButton = new Button(composite_1, SWT.PUSH | SWT.BORDER);
		loginButton.setText("Login to PCF");
		loginButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//call Login Wizard
				PCFLoginWizard loginWizard =	new PCFLoginWizard(project);	
				PCFLoginWizardDialog dialog = new PCFLoginWizardDialog(container.getShell(),loginWizard);
				dialog.setPageSize(80, 150);
				if (dialog.open() == Window.OK) 
				{
					if(tableViewer != null && !tableViewer.getTable().isDisposed()) {
						tableViewer.getTable().dispose();
						container.getChildren()[1].dispose();;
					}
					project = loginWizard.getProject();
					for( BWModule module : project.getModules() )
					{
						if( module.getType() == BWModuleType.Application )
						{
							appmodule=module;
							break;
						}
					}
					client=appmodule.getBwpcfModule().getClient();
					pcfServices=getPCFServices();
					setApplicationServicesPCFPOMFields();
					container.layout();
				}

			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		client=appmodule.getBwpcfModule().getClient();
		if(client!=null){
			pcfServices=getPCFServices();
			setApplicationServicesPCFPOMFields();
		}
		setControl(container);
		setPageComplete(true);
	}
	
	
	private void setApplicationServicesPCFPOMFields() 
	{
		final Composite composite_1 = new Composite(container, SWT.BORDER);
        composite_1.setLayout(new GridLayout(4, false));
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		 tableViewer = new TableViewer(composite_1, SWT.MULTI | SWT.H_SCROLL
			      | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
		
		 tableViewer.setContentProvider(new ArrayContentProvider());
		 
		Table table = tableViewer.getTable();
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 4;
		table.setLayoutData(data);
		
		String[] titles = {" Service Name","Service Label", "Service Version", "Service Plan"  };
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
		}	
		
		if(pcfServices.size()>0){
			for (CloudService service : pcfServices) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(new String[] {service.getName(), service.getLabel(), service.getVersion(), service.getPlan()});
				appPCFServices.add(item);
			}
		}
		
		
		for (int i=0; i<titles.length; i++) {
			table.getColumn (i).pack ();
		}
		table.setSize(table.computeSize(SWT.DEFAULT, pcfServices.size()));
		
		if(tableViewer != null && !tableViewer.getTable().isDisposed()) {
			while (tableViewer.isBusy()) {
				// Do nothing, only wait for tableViewer to not be bussy
			}
			//tableViewer.refresh(appPCFServices);
		}
		
		// editing the columns
		//final int EDITABLECOLUMN_1 = 0;
		//addTableSelectionListner(table, EDITABLECOLUMN_1);
	}
	
	private List<CloudService> getPCFServices(){
		
		return client.getServices();
        
	}
	
	
	public BWProject getSelectedServices() 
	{
		List<BWPCFServicesModule> services=new ArrayList<BWPCFServicesModule>();
		for(TableItem service:appPCFServices)
		{
			if(service.getChecked())
			{
				BWPCFServicesModule serviceMod=new BWPCFServicesModule();
				if(service.getText(0)!=null && !service.getText(0).equals(""))
				{
					serviceMod.setServiceName(service.getText(0));
					serviceMod.setServiceLabel(service.getText(1));
					serviceMod.setServiceVersion(service.getText(2));
					serviceMod.setServicePlan(service.getText(3));
					services.add(serviceMod);
				}
			}
		}
		
		
		BWPCFModule bwpcf=appmodule.getBwpcfModule();
		bwpcf.setServices(services);
		for( BWModule module : project.getModules() )
		{
			if( module.getType() == BWModuleType.Application )
			{
				module.setBwpcfModule(bwpcf);
				break;
			}
		}
		
		return project;
	}
}
