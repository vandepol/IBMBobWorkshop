# Migration Guide: Struts 2 to Angular + JAX-RS

This document explains the step-by-step migration from the legacy Struts 2 application to the modern Angular + JAX-RS architecture.

## Overview

The migration involved transforming a server-side rendered Struts 2 application into a modern single-page application (SPA) with a RESTful API backend.

## Migration Steps

### 1. Backend Modernization

#### 1.1 Remove Struts Dependencies
**Before (pom.xml):**
```xml
<dependency>
    <groupId>org.apache.struts</groupId>
    <artifactId>struts2-core</artifactId>
    <version>2.5.30</version>
</dependency>
```

**After (pom.xml):**
```xml
<dependency>
    <groupId>javax.ws.rs</groupId>
    <artifactId>javax.ws.rs-api</artifactId>
    <version>2.1</version>
    <scope>provided</scope>
</dependency>
```

#### 1.2 Convert Struts Actions to JAX-RS Resources

**Before (DashboardAction.java):**
```java
public class DashboardAction extends ActionSupport {
    private List<Prescription> pendingPrescriptions;
    
    public String execute() {
        pendingPrescriptions = prescriptionRepo.findByStatus("PENDING");
        return SUCCESS;
    }
    
    public List<Prescription> getPendingPrescriptions() {
        return pendingPrescriptions;
    }
}
```

**After (DashboardResource.java):**
```java
@Path("/dashboard")
public class DashboardResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDashboardData() {
        List<Prescription> pendingPrescriptions = prescriptionRepo.findByStatus("PENDING");
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("pendingPrescriptions", pendingPrescriptions);
        return dashboard;
    }
}
```

#### 1.3 Update Liberty Configuration

**Before (server.xml):**
```xml
<featureManager>
    <feature>jsp-2.3</feature>
    <feature>servlet-3.1</feature>
</featureManager>
```

**After (server.xml):**
```xml
<featureManager>
    <feature>jaxrs-2.0</feature>
    <feature>jsonp-1.0</feature>
    <feature>servlet-3.1</feature>
</featureManager>
```

#### 1.4 Remove Struts Configuration

**Removed:**
- `src/main/resources/struts.xml`
- Struts filter from `web.xml`
- All JSP files from `src/main/webapp/WEB-INF/content/`

### 2. Frontend Modernization

#### 2.1 Create Angular Project Structure

```bash
mkdir frontend
cd frontend
# Created Angular configuration files
```

#### 2.2 Implement TypeScript Models

**Example (prescription.model.ts):**
```typescript
export interface Prescription {
  id: string;
  patientName: string;
  patientId: string;
  doctorName: string;
  medicineId: string;
  medicineName: string;
  quantity: number;
  dosage: string;
  prescriptionDate: string;
  expiryDate: string;
  status: 'PENDING' | 'VALIDATED' | 'FULFILLED' | 'EXPIRED';
  notes: string;
}
```

#### 2.3 Create API Service

**pharmacy.service.ts:**
```typescript
@Injectable({
  providedIn: 'root'
})
export class PharmacyService {
  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  getDashboardData(): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${this.apiUrl}/dashboard`);
  }
  
  // Additional methods...
}
```

#### 2.4 Build Angular Components

**Before (dashboard.jsp):**
```jsp
<%@ taglib prefix="s" uri="/struts-tags" %>
<h2>Dashboard</h2>
<table>
    <s:iterator value="pendingPrescriptions">
        <tr>
            <td><s:property value="id"/></td>
            <td><s:property value="patientName"/></td>
        </tr>
    </s:iterator>
</table>
```

**After (dashboard.component.ts):**
```typescript
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <h2>Dashboard</h2>
    <table>
      <tr *ngFor="let prescription of dashboardData?.pendingPrescriptions">
        <td>{{ prescription.id }}</td>
        <td>{{ prescription.patientName }}</td>
      </tr>
    </table>
  `
})
export class DashboardComponent implements OnInit {
  dashboardData: DashboardData | null = null;
  
  ngOnInit(): void {
    this.pharmacyService.getDashboardData().subscribe(data => {
      this.dashboardData = data;
    });
  }
}
```

### 3. API Design

#### REST Endpoints Mapping

| Struts Action | HTTP Method | REST Endpoint |
|--------------|-------------|---------------|
| dashboard | GET | /api/dashboard |
| medicine-list | GET | /api/medicines |
| medicine-view | GET | /api/medicines/{id} |
| prescription-list | GET | /api/prescriptions |
| prescription-create | POST | /api/prescriptions |
| prescription-validate | PUT | /api/prescriptions/{id}/validate |
| order-list | GET | /api/orders |
| order-view | GET | /api/orders/{id} |
| order-processPayment | PUT | /api/orders/{id}/payment |

### 4. Data Flow Changes

#### Before (Struts)
```
Browser → Struts Filter → Action → JSP → HTML Response
```

#### After (Angular + REST)
```
Browser → Angular App → HTTP Request → JAX-RS Resource → JSON Response → Angular Component
```

### 5. Key Improvements

#### 5.1 Separation of Concerns
- **Before**: Presentation logic mixed with business logic in Actions
- **After**: Clear separation between frontend (Angular) and backend (JAX-RS)

#### 5.2 API Reusability
- **Before**: JSP views tightly coupled to Struts
- **After**: REST API can be consumed by any client (web, mobile, etc.)

#### 5.3 Modern UI/UX
- **Before**: Server-side rendered HTML with limited interactivity
- **After**: Single-page application with smooth transitions and modern design

#### 5.4 Type Safety
- **Before**: No compile-time type checking in JSP
- **After**: TypeScript provides strong typing throughout the frontend

#### 5.5 Development Experience
- **Before**: Full server restart for most changes
- **After**: Hot module replacement in Angular, faster development cycle

### 6. Testing Strategy

#### Backend Testing
```java
// Example JAX-RS resource test
@Test
public void testGetDashboard() {
    Response response = target("/api/dashboard").request().get();
    assertEquals(200, response.getStatus());
}
```

#### Frontend Testing
```typescript
// Example Angular component test
it('should load dashboard data', () => {
  component.ngOnInit();
  expect(component.dashboardData).toBeDefined();
});
```

### 7. Deployment Changes

#### Before
```bash
mvn clean package
# Deploy simple-pharmacy.war to Liberty
```

#### After
```bash
# Build frontend
cd frontend && npm run build

# Copy frontend build to webapp
cp -r dist/pharmacy-frontend/* ../src/main/webapp/

# Build backend
cd .. && mvn clean package

# Deploy to Liberty
```

### 8. Common Challenges & Solutions

#### Challenge 1: CORS Issues
**Solution**: Implemented CorsFilter.java to handle cross-origin requests during development

#### Challenge 2: Date Serialization
**Solution**: Used ISO 8601 format for dates in JSON

#### Challenge 3: Session Management
**Solution**: Moved to stateless REST API, can add JWT tokens if needed

#### Challenge 4: Form Validation
**Solution**: Implemented validation in both Angular (client-side) and JAX-RS (server-side)

### 9. Performance Improvements

- **Initial Load**: Slightly slower (Angular bundle download)
- **Subsequent Navigation**: Much faster (no page reloads)
- **API Calls**: More efficient (JSON vs full HTML pages)
- **Caching**: Better browser caching of static assets

### 10. Future Enhancements

- [ ] Add JWT authentication
- [ ] Implement database persistence
- [ ] Add comprehensive unit tests
- [ ] Implement CI/CD pipeline
- [ ] Add Docker containerization
- [ ] Implement server-side rendering (SSR) for SEO
- [ ] Add Progressive Web App (PWA) features

## Conclusion

This migration demonstrates a successful transformation from a legacy Struts 2 application to a modern, maintainable architecture. The new stack provides better separation of concerns, improved developer experience, and a foundation for future enhancements.

## Resources

- [Angular Documentation](https://angular.io/docs)
- [JAX-RS Specification](https://jakarta.ee/specifications/restful-ws/)
- [Open Liberty Documentation](https://openliberty.io/docs/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

---

**Made with Bob** 🤖