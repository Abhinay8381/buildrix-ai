import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, LogOut, Search, Folder, Loader2, MoreVertical, Trash, Download, Edit } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { api, removeAuthToken, removeUserInfo, getUserInfo } from "@/lib/api";
import { ProjectSummaryResponse } from "@/lib/types";
import { useToast } from "@/hooks/use-toast";
import { generateGradient, cn } from "@/lib/utils";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";

export function ProjectsDashboard() {
    const navigate = useNavigate();
    const { toast } = useToast();
    const [projects, setProjects] = useState<ProjectSummaryResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState("");
    const [isCreating, setIsCreating] = useState(false);
    const [newProjectName, setNewProjectName] = useState("");
    const [isDialogOpen, setIsDialogOpen] = useState(false);

    // Rename state
    const [isRenameDialogOpen, setIsRenameDialogOpen] = useState(false);
    const [projectToRename, setProjectToRename] = useState<ProjectSummaryResponse | null>(null);
    const [renameName, setRenameName] = useState("");

    useEffect(() => {
        fetchProjects();
    }, []);

    const fetchProjects = async () => {
        try {
            const data = await api.getProjects();
            setProjects(data);
        } catch (error) {
            console.error("Failed to fetch projects:", error);
            toast({
                title: "Error",
                description: "Failed to load projects. Please try again.",
                variant: "destructive",
            });
        } finally {
            setLoading(false);
        }
    };

    const handleCreateProject = async () => {
        if (!newProjectName.trim()) return;

        setIsCreating(true);
        try {
            const newProject = await api.createProject(newProjectName);
            setProjects([newProject, ...projects]);
            setNewProjectName("");
            setIsDialogOpen(false);
            toast({
                title: "Success",
                description: "Project created successfully",
            });
            // Optionally navigate to the new project immediately
            // navigate(`/projects/${newProject.id}`);
        } catch (error) {
            console.error("Failed to create project:", error);
            toast({
                title: "Error",
                description: "Failed to create project",
                variant: "destructive",
            });
        } finally {
            setIsCreating(false);
        }
    };

    const handleDeleteProject = async (e: React.MouseEvent, projectId: number) => {
        e.stopPropagation();
        if (!confirm("Are you sure you want to delete this project? This action cannot be undone.")) return;

        try {
            await api.deleteProject(projectId.toString());
            setProjects(projects.filter(p => p.id !== projectId));
            toast({ title: "Success", description: "Project deleted successfully" });
        } catch (error) {
            console.error("Failed to delete:", error);
            toast({ title: "Error", description: "Failed to delete project", variant: "destructive" });
        }
    };

    const handleDownloadProject = async (e: React.MouseEvent, projectId: number) => {
        e.stopPropagation();
        try {
            const blob = await api.downloadProjectZip(projectId.toString());
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `project-${projectId}.zip`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            toast({ title: "Success", description: "Download started" });
        } catch (error) {
            console.error("Failed to download:", error);
            toast({ title: "Error", description: "Failed to download project", variant: "destructive" });
        }
    };

    const handleRenameClick = (e: React.MouseEvent, project: ProjectSummaryResponse) => {
        e.stopPropagation();
        setProjectToRename(project);
        setRenameName(project.name);
        setIsRenameDialogOpen(true);
    };

    const handleRenameSubmit = async () => {
        if (!projectToRename || !renameName.trim()) return;

        try {
            await api.updateProject(projectToRename.id.toString(), renameName);
            setProjects(projects.map(p => p.id === projectToRename.id ? { ...p, name: renameName } : p));
            setIsRenameDialogOpen(false);
            setProjectToRename(null);
            toast({ title: "Success", description: "Project renamed successfully" });
        } catch (error) {
            console.error("Failed to rename:", error);
            toast({ title: "Error", description: "Failed to rename project", variant: "destructive" });
        }
    };

    const handleLogout = () => {
        removeAuthToken();
        removeUserInfo();
        navigate("/login");
    };

    const filteredProjects = projects.filter((project) =>
        project.name.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
        <div className="min-h-screen bg-background font-mono text-foreground">
            {/* Header */}
            <header className="border-b-2 border-primary/40 bg-card/95 backdrop-blur shadow-[0_0_15px_rgba(255,170,0,0.1)]">
                <div className="container flex h-16 max-w-screen-2xl items-center justify-between px-4 sm:px-8">
                    <div className="flex items-center gap-3 font-mono font-bold text-lg tracking-wider text-primary uppercase">
                        <img src="/assets/buildrix-ai.svg" alt="Buildrix AI" className="w-8 h-8 object-contain" />
                        <span>BUILDRIX AI // DASHBOARD</span>
                    </div>
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="icon" className="h-10 w-10 rounded-sm border border-primary/40 bg-background/50 hover:bg-primary/20 hover:border-primary">
                                <Avatar className="h-9 w-9 rounded-sm">
                                    <AvatarFallback className="bg-primary/20 text-primary font-bold font-mono">
                                        {(() => {
                                            const userInfo = getUserInfo();
                                            if (userInfo?.name) {
                                                return userInfo.name.charAt(0).toUpperCase();
                                            }
                                            return "U";
                                        })()}
                                    </AvatarFallback>
                                </Avatar>
                            </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="w-56 bg-card border-2 border-primary/40 font-mono shadow-[0_0_20px_rgba(255,170,0,0.2)]">
                            <div className="flex flex-col space-y-1 p-2 border-b border-primary/20">
                                <p className="text-xs font-bold leading-none text-primary">
                                    {getUserInfo()?.name || "User"}
                                </p>
                                <p className="text-[10px] leading-none text-muted-foreground">
                                    {getUserInfo()?.email || getUserInfo()?.username || ""}
                                </p>
                            </div>
                            <DropdownMenuItem onClick={handleLogout} className="text-destructive focus:text-destructive cursor-pointer font-mono text-xs">
                                <LogOut className="w-4 h-4 mr-2" />
                                [ DISCONNECT ]
                            </DropdownMenuItem>
                        </DropdownMenuContent>
                    </DropdownMenu>
                </div>
            </header>

            <main className="container max-w-screen-2xl py-8 px-4 sm:px-8">
                <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mb-8">
                    <div>
                        <h1 className="text-3xl font-bold tracking-widest text-primary uppercase font-mono">PROJECT REPOSITORY</h1>
                        <p className="text-muted-foreground text-xs font-mono mt-1">
                            System initialized. Manage your Buildrix AI applications.
                        </p>
                    </div>

                    <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                        <DialogTrigger asChild>
                            <Button className="gap-2 bg-primary hover:bg-primary/90 text-primary-foreground font-mono font-bold tracking-wider uppercase rounded-sm shadow-[0_0_15px_rgba(255,170,0,0.3)]">
                                <Plus className="w-4 h-4" />
                                + NEW PROJECT
                            </Button>
                        </DialogTrigger>
                        <DialogContent>
                            <DialogHeader>
                                <DialogTitle>Create New Project</DialogTitle>
                                <DialogDescription>
                                    Give your project a name to get started. You can change this later.
                                </DialogDescription>
                            </DialogHeader>
                            <div className="py-4">
                                <Input
                                    placeholder="My Awesome Project"
                                    value={newProjectName}
                                    onChange={(e) => setNewProjectName(e.target.value)}
                                    onKeyDown={(e) => e.key === "Enter" && handleCreateProject()}
                                />
                            </div>
                            <DialogFooter>
                                <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                                    Cancel
                                </Button>
                                <Button onClick={handleCreateProject} disabled={isCreating || !newProjectName.trim()}>
                                    {isCreating && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                    Create Project
                                </Button>
                            </DialogFooter>
                        </DialogContent>
                    </Dialog>

                    {/* Rename Dialog */}
                    <Dialog open={isRenameDialogOpen} onOpenChange={setIsRenameDialogOpen}>
                        <DialogContent>
                            <DialogHeader>
                                <DialogTitle>Rename Project</DialogTitle>
                            </DialogHeader>
                            <div className="py-4">
                                <Input
                                    value={renameName}
                                    onChange={(e) => setRenameName(e.target.value)}
                                    onKeyDown={(e) => e.key === "Enter" && handleRenameSubmit()}
                                />
                            </div>
                            <DialogFooter>
                                <Button variant="outline" onClick={() => setIsRenameDialogOpen(false)}>Cancel</Button>
                                <Button onClick={handleRenameSubmit} disabled={!renameName.trim() || renameName === projectToRename?.name}>
                                    Save
                                </Button>
                            </DialogFooter>
                        </DialogContent>
                    </Dialog>
                </div>

                {/* Search */}
                <div className="relative mb-8 max-w-md">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-primary" />
                    <Input
                        placeholder="Search project repository..."
                        className="pl-10 h-11 bg-card border-primary/30 focus:border-primary rounded-sm font-mono text-xs text-foreground"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                    />
                </div>

                {/* Grid */}
                {loading ? (
                    <div className="flex items-center justify-center py-20">
                        <Loader2 className="w-8 h-8 animate-spin text-primary" />
                    </div>
                ) : filteredProjects.length === 0 ? (
                    <div className="text-center py-20 border-2 border-dashed border-primary/30 rounded-sm bg-card/40">
                        <h3 className="text-lg font-bold text-primary uppercase tracking-wider mb-2 font-mono">No projects found</h3>
                        <p className="text-muted-foreground text-xs font-mono mb-6">
                            {searchQuery ? "No repository entry matches search query" : "Initialize your first Buildrix AI project"}
                        </p>
                        {!searchQuery && (
                            <Button onClick={() => setIsDialogOpen(true)} className="bg-primary text-primary-foreground font-mono font-bold uppercase rounded-sm">
                                Create Project
                            </Button>
                        )}
                    </div>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {filteredProjects.map((project) => (
                            <Card
                                key={project.id}
                                className="group cursor-pointer bg-card border-2 border-primary/30 hover:border-primary transition-all shadow-[0_0_15px_rgba(0,0,0,0.5)] hover:shadow-[0_0_20px_rgba(255,170,0,0.2)] rounded-sm overflow-hidden font-mono"
                                onClick={() => navigate(`/projects/${project.id}`)}
                            >
                                <CardHeader className="p-0">
                                    <div className="aspect-video bg-muted/30 w-full relative overflow-hidden border-b border-primary/20">
                                        {project.thumbnailUrl ? (
                                            <img
                                                src={project.thumbnailUrl}
                                                alt={project.name}
                                                className="w-full h-full object-cover transition-transform group-hover:scale-105"
                                            />
                                        ) : (
                                            <div className="w-full h-full bg-[#eee3cf] flex flex-col items-center justify-center p-4 relative overflow-hidden border-b-2 border-[#241d17]">
                                                {/* Retro background grid */}
                                                <div 
                                                    className="absolute inset-0 opacity-30 pointer-events-none"
                                                    style={{
                                                        backgroundImage: "linear-gradient(#cbbda5 1px, transparent 1px), linear-gradient(90deg, #cbbda5 1px, transparent 1px)",
                                                        backgroundSize: "20px 20px"
                                                    }}
                                                />
                                                {/* App Logo & Letter Badge */}
                                                <div className="relative z-10 flex flex-col items-center gap-2">
                                                    <div className="w-12 h-12 bg-[#e85d32] border-2 border-[#241d17] shadow-[3px_3px_0_#241d17] flex items-center justify-center">
                                                        <img src="/assets/buildrix-ai.svg" alt="Buildrix AI Logo" className="w-8 h-8 object-contain" />
                                                    </div>
                                                    <span className="text-[10px] font-mono font-bold tracking-widest text-[#766b5e] uppercase">
                                                        // WORKSPACE
                                                    </span>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </CardHeader>
                                <CardContent className="p-4 flex flex-col gap-2">
                                    <div className="flex justify-between items-start gap-2">
                                        <CardTitle className="text-base font-bold tracking-wide text-foreground group-hover:text-primary transition-colors line-clamp-1 uppercase">
                                            {project.name}
                                        </CardTitle>
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild onClick={(e) => e.stopPropagation()}>
                                                <Button variant="ghost" size="icon" className="h-8 w-8 -mt-1 -mr-2 text-muted-foreground hover:text-primary">
                                                    <MoreVertical className="w-4 h-4" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent align="end" className="bg-card border-2 border-primary/40 font-mono text-xs shadow-[0_0_15px_rgba(255,170,0,0.2)]">
                                                <DropdownMenuItem onClick={(e) => handleRenameClick(e, project)} className="cursor-pointer">
                                                    <Edit className="w-4 h-4 mr-2 text-primary" />
                                                    Rename
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={(e) => handleDownloadProject(e, project.id)} className="cursor-pointer">
                                                    <Download className="w-4 h-4 mr-2 text-secondary" />
                                                    Download
                                                </DropdownMenuItem>
                                                <DropdownMenuItem className="text-destructive focus:text-destructive cursor-pointer" onClick={(e) => handleDeleteProject(e, project.id)}>
                                                    <Trash className="w-4 h-4 mr-2" />
                                                    Delete
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </div>
                                    {project.role && (
                                        <div className="flex">
                                            <span className={cn(
                                                "text-[10px] font-mono font-bold uppercase tracking-wider px-2 py-0.5 rounded-sm border",
                                                project.role === 'OWNER' ? "bg-primary/20 text-primary border-primary/40" :
                                                    project.role === 'EDITOR' ? "bg-secondary/20 text-secondary border-secondary/40" :
                                                        "bg-muted text-muted-foreground border-border"
                                            )}>
                                                {project.role}
                                            </span>
                                        </div>
                                    )}
                                </CardContent>
                                <CardFooter className="p-4 pt-0 text-[11px] font-mono text-muted-foreground">
                                    UPDATED: {new Date(project.createdAt).toLocaleDateString()}
                                </CardFooter>
                            </Card>
                        ))}
                    </div>
                )}
            </main>
        </div>
    );
}
